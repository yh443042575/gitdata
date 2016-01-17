package edu.hit.yh.gitdata.githubDataAnalyzer;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import com.mysql.jdbc.StringUtils;

import edu.hit.yh.gitdata.githubDataModel.ArtifactOwner;
import edu.hit.yh.gitdata.githubDataModel.CommitCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.githubDataModel.IssueCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.IssuesEvent;
import edu.hit.yh.gitdata.githubDataModel.PullRequestEvent;
import edu.hit.yh.gitdata.githubDataModel.PullRequestReviewCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.PushEvent;

/**
 * 寻找行为的接收者
 * 1、接受者可能直接来自用户的行为内容中，比如言论，或者assignee
 * 2、接收者可能也由Artifact产生
 * 
 * 问题：如何能判断出来某个artifact是与其他artifact有关系，从githubArchive上能看出来么。。。妈了个叉的。。。
 * @author DHAO
 *
 */
public class TargetFinder {
	
	private static final Pattern PATTERN = Pattern.compile("^((https|http|ftp|rtsp|mms)?://)" 
		     + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" 
		     + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" 
		     + "|" 
		     + "([0-9a-z_!~*'()-]+\\.)*" 
		     + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." 
		     + "[a-z]{2,6})" 
		     + "(:[0-9]{1,4})?" 
		     + "((/?)|" 
		     + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");
	
	
	/*--------------------------基本方法------------------------------*/
	/**
	 * 通过开发者每一次行为的谈话内容，识别出该行为是针对谁的
	 * @param content
	 * @return
	 */
	public static String findTargetInArtifactByContent(String content){
		if(!StringUtils.isNullOrEmpty(content)){
			StringBuffer target = new StringBuffer("");
			Pattern pattern = Pattern.compile("@[a-zA-Z]+ ");
			Matcher matcher = pattern.matcher(content);
			while(matcher.find()){
				target.append(matcher.group()+"+");
			}
			return target.toString();
		}else {
			return null;
		}
	}
	
	/**
	 * 通过htmlUrl来找到artifact之间的作用关系
	 * @param url
	 * @return
	 */
	public static String findTargetAmongArtifactsByUrl(String url) {
		
		String target="";
		Matcher matcher = PATTERN.matcher(url);
		//验证输入的字符串是否为url
		if(!matcher.find()){
			System.out.println("不合法的网址："+url);
			return null;
		}else{//如果url正确则寻找对应的fix Artifact
			HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
			NodeList nodeList = htmlAnalyzer.getNodeList(url);
			
			//如果请求失败，返回空值
			if(nodeList == null){
				return null;
			}
			target = processUrlTargetNodeList(nodeList,target);
		}
		
		if(target.equals("")){
			target = "no target to find";
		}
		
		Session	session = HibernateUtil.getSessionFactory().openSession();
		
		String sql =  "select artifactId,owner from artifactowner where artifactId in ("+target+")";
		List<Object[]> list = session.createSQLQuery(sql).list();
		for(Object[] objects:list){
			for(int i = 0;i<objects.length;i++){
				if(objects[i]!=null){
					System.out.print(objects[i].toString());
				}
				else {
					System.out.println("null");
				}
				System.out.println(" ");
			}
		System.out.println();
		}
		
		
		return target;
	}
	
	public static String findTargetAmongArtifactsByArtifacts(String artifacts,Session session) {
		if(!StringUtils.isNullOrEmpty(artifacts)){
			StringBuffer sb = new StringBuffer("");
			String []artifactIds = artifacts.split(" ");
			for(String s:artifactIds){
				Query query = session.createSQLQuery("select distinct(owner) from artifactowner where artifactId = 'https://github.com/"+s+"'");
				List<String> ownerList = query.list();
				for(String o:ownerList){
					sb.append(o+"+");
				}
			}
			System.out.println("atar"+sb.toString());
			return sb.toString();
		}else {
			return null;
		}
		
		
		
	}
	
	
	/**
	 * 
	 * 处理TargetNodeList，进而得到target
	 * @param nodeList
	 * @param target
	 */
	private static String processUrlTargetNodeList(NodeList nodeList,String target) {
		SimpleNodeIterator iterator = nodeList.elements();
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			// System.out.println(node.getText());
			if (node.getText().matches(
					"div class=\"commit-desc\".*+")) {// add
				target =  handleUrlTarget(node);
			}else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				
				// 孩子节点为空，说明是值节点
				if (null != childList) {
					target = processUrlTargetNodeList(childList,target);
					// 若包含关键字，则简单打印出来文本
					// end if
				}	
			}
		}// end while
		return target;
	}
	
	/**
	 * 找到我们需要的内容所在的node之后，我们就去分析本次行为都指向了哪些artifact
	 * @param node
	 * @return
	 */
	private static String handleUrlTarget(Node node) {
		
		StringBuilder sb = new StringBuilder();
		Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/(commit|issues|pull)/[a-z[0-9]]+");
		
		Matcher matcher = artifactPattern.matcher(node.getChildren().toString());
		while(matcher.find()){
			sb.append("'");
			sb.append(matcher.group());
			sb.append("'");
			sb.append(",");
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	/*-----------------------对外暴露的方法----------------------------*/
	/**
	 * 以下则是针对各种event的分析
	 * 通过两个维度去寻找某个行为所针对的对象
	 * @param repo
	 */
	public static void findCommitCommentEventTarget(String repo,Session session){
		Query query = session.createSQLQuery("select * from CommitCommentEvent where sourceType = 'net' ").addEntity(CommitCommentEvent.class);
		List<CommitCommentEvent> ccList = query.list();
		for(CommitCommentEvent e:ccList){
			String ctar = findTargetInArtifactByContent(e.getBody());
			if(!StringUtils.isNullOrEmpty(ctar)){
				e.setTarget(ctar);
				session.beginTransaction();
				session.update(e);
				session.getTransaction().commit();
			}
			System.out.println(e.getArtifactId()+" ctar:"+ctar);
			e.setTarget(ctar);
		}
	}
	
	public static void findIssueCommentEventTarget(String repo,Session session){
		Query query = session.createSQLQuery("select * from IssueCommentEvent where sourceType = 'net' ").addEntity(IssueCommentEvent.class);
		List<IssueCommentEvent> icList = query.list();
		for(IssueCommentEvent e:icList){
			String ctar = findTargetInArtifactByContent(e.getCommentBody());
			if(!StringUtils.isNullOrEmpty(ctar)){
				e.setTarget(ctar);
				session.beginTransaction();
				session.update(e);
				session.getTransaction().commit();
			}
			System.out.println(e.getArtifactId()+" ctar:"+ctar);
			e.setTarget(ctar);
		}
	}
	
	public static void findIssuesEventTarget(String repo,Session session){
		Query query = session.createSQLQuery("select * from IssuesEvent where sourceType = 'net' ").addEntity(IssuesEvent.class);
		List<IssuesEvent> iList = query.list();
		for(IssuesEvent e:iList){
			String atar = "";
			if(e.getIssueAction().equals("ref")){
				atar = findTargetAmongArtifactsByArtifacts(e.getDesContent1(), session); 
			}else if(e.getIssueAction().equals("assignee")){
				atar +=e.getIssueContent(); 
			}
			if(!StringUtils.isNullOrEmpty(atar)){
				e.setTarget(atar);
				session.beginTransaction();
				session.update(e);
				session.getTransaction().commit();
			}
			System.out.println(e.getArtifactId()+" atar:"+atar);
		}
	}
	
	/**
	 * 1、对于指派动作来说，作用目标为被指派的人
	 * 2、对于ref动作来说，作用目标为所涉及到的artifact的拥有者
	 * @param repo
	 * @param session
	 */
	public static void findPullrequestEventTarget(String repo,Session session){
		Query query = session.createSQLQuery("select * from PullrequestEvent where sourceType = 'net' ").addEntity(PullRequestEvent.class);
		List<PullRequestEvent> pList = query.list();
		for(PullRequestEvent e:pList){
			String atar = "";
			if(e.getAction().equals("ref")){
				atar = findTargetAmongArtifactsByArtifacts(e.getPullrequestBaseRef(), session); 
			}else if(e.getAction().equals("assignee")){
				atar +=e.getPullrequestAssgnee(); 
			}
			if(!StringUtils.isNullOrEmpty(atar)){
				e.setTarget(atar);
				session.beginTransaction();
				session.update(e);
				session.getTransaction().commit();
			}
			System.out.println(e.getArtifactId()+" atar:"+atar);
		}
	}
	
	public static void findPullrequestReviewCommentTarget(String repo,Session session){
		Query query = session.createSQLQuery("select * from PullRequestReviewCommentEvent where sourceType = 'net' ").addEntity(PullRequestReviewCommentEvent.class);
		List<PullRequestReviewCommentEvent> prList = query.list();
		for(PullRequestReviewCommentEvent e:prList){
			String ctar = findTargetInArtifactByContent(e.getCommentBody());
			if(!StringUtils.isNullOrEmpty(ctar)){
				e.setTarget(ctar);
				session.beginTransaction();
				session.update(e);
				session.getTransaction().commit();
			}
			System.out.println(e.getArtifactId()+" ctar:"+ctar);
			e.setTarget(ctar);
		}
	}
	
	public static void findPushEventTarget(String repoString ,Session session){
		Query query = session.createSQLQuery("select * from pushEvent where sourceType = 'net' ").addEntity(PushEvent.class);
		List<PushEvent> pList = query.list();
		for(PushEvent e:pList){
			String atar = findTargetAmongArtifactsByArtifacts(e.getTitle(), session);
			if(!StringUtils.isNullOrEmpty(atar)){
				e.setTarget(atar);
				session.beginTransaction();
				session.update(e);
				session.getTransaction().commit();
			}
			System.out.println(e.getArtifactId()+" atar:"+atar);
		}
	}
	
	public static void main(String args[]){
		Session session = HibernateUtil.getSessionFactory().openSession();
		//TargetFinder.findCommitCommentEventTarget("jquery/jquery/", session);
		//TargetFinder.findPushEventTarget("jquery/jquery/", session);
		//TargetFinder.findIssueCommentEventTarget("jquery/jquery/", session);
		//TargetFinder.findPullrequestEventTarget("jquery/jquery/", session);
		//TargetFinder.findIssuesEventTarget("jquery/jquery/", session);
		TargetFinder.findPullrequestReviewCommentTarget("jquery/jquery/", session);
		HibernateUtil.closeSessionFactory();
	}
	

}
