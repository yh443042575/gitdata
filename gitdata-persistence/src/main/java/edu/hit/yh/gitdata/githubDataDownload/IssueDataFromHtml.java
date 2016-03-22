package edu.hit.yh.gitdata.githubDataDownload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.Session;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.hit.yh.gitdata.githubDataAnalyzer.HtmlAnalyzer;
import edu.hit.yh.gitdata.githubDataModel.ArtifactOwner;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.githubDataModel.IssueCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.IssuesEvent;

/**
 * 补全github_archive数据库中的数据
 * 
 * 大体思路：
 *  1、先得到网页的源代码，
 *  2、解析每一类行为
 * 
 * @author DHAO
 *
 */
public class IssueDataFromHtml {
	
	private HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
	
	@Getter
	@Setter
	private Session session;
	/**
	 * 要解析的repository
	 */
	@Getter
	@Setter
	private String repo;
	
	/*------------------------解析方法------------------------*/
	
	/**
	 * 处理开启issue动作
	 * @param source
	 */
	public List<IssuesEvent> processOpenIssue(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("id=\"partial-discussion-header\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("open");
				Node titleNode = DownloadUtil.getSomeChild(node, "span class=\"js-issue-title\"");
				issuesEvent.setIssueContent(titleNode.toPlainTextString());
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processOpenIssue(childList,iList);
				}
			}
		}
		return iList;
	}
	
	/**
	 * 提取网页中所有的IssueComment元素
	 * @param source
	 */
	private List<IssueCommentEvent> processComment(NodeList nodeList,List<IssueCommentEvent> icList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().matches("div id=\"issuecomment-.*\".*+")) {
				IssueCommentEvent i = new IssueCommentEvent();
				//TODO 解析comment工作
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				i.setActor(actorNode.toPlainTextString());
				Node contentNode = DownloadUtil.getSomeChild(node, "div class=\"comment-body");
				//i.setCommentBody(contentNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					i.setCreatedAt(time);
					System.out.println(time);
				}
				icList.add(i);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processComment(childList,icList);
				}
			}
		}
		return icList;
	}
	
	/**
	 * 处理Reference了当前issue的操作
	 * @param source
	 */
	public List<IssuesEvent> processReference(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("div class=\"discussion-item discussion-item-ref\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("ref");
				Node anotherAtifactNode = DownloadUtil.getSomeChild(node, "class=\"title-link\"");
				issuesEvent.setIssueContent(anotherAtifactNode==null?"":anotherAtifactNode.toPlainTextString());
				Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/[a-z[0-9]]+");
			//	Pattern artifactPattern = Pattern.compile("zurb/foundation-sites/[a-zA-Z]+/[a-z[0-9]]+");

				//Pattern milestonePattern = Pattern.compile("zurb/foundation-sites/[a-zA-Z]+/.*+");
				Matcher artifactMatcher = artifactPattern.matcher(anotherAtifactNode==null?"":anotherAtifactNode.getText());
				if(artifactMatcher.find()){
					String anotherAtifact = artifactMatcher.group();
					issuesEvent.setDesContent1(anotherAtifact);
					System.out.println(anotherAtifact);
				}
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode==null?"":actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processReference(childList,iList);
				}
			}
		}
		return iList;
	}
	
	/**
	 * 处理关闭issue的操作
	 * @param source
	 */
	private List<IssuesEvent> processCloseIssue(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("div class=\"discussion-item discussion-item-closed\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("close");
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode==null?"null":actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				if(timeNode!=null){
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processCloseIssue(childList,iList);
				}
			}
		}
		return iList;
	}
	/**
	 * 处理labeled操作
	 * @param source
	 */
	public List<IssuesEvent> processLabled(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"discussion-item discussion-item-labeled\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("labeled");
				List<Node> lableList = new ArrayList<Node>(); 
				lableList= DownloadUtil.getLableList(node, "style=\"color:", lableList);
				String lables = "";
				for(int i=0;i<lableList.size();i++){
					lables+=lableList.get(i).toPlainTextString();
					if(i!=lableList.size()-1){
						lables+=",";
					}
				}
				System.out.println(lables);
				issuesEvent.setIssueLabels(lables);
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processLabled(childList,iList);
				}
			}
		}
		return iList;
	}
	
	/**
	 * 处理unlabeled
	 */
	public List<IssuesEvent> processUnLabled(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"discussion-item discussion-item-unlabeled")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("unlabeled");
				List<Node> lableList = new ArrayList<Node>(); 
				lableList= DownloadUtil.getLableList(node, "style=\"color:", lableList);
				String lables = "";
				for(int i=0;i<lableList.size();i++){
					lables+=lableList.get(i).toPlainTextString();
					if(i!=lableList.size()-1){
						lables+=",";
					}
				}
				System.out.println(lables);
				issuesEvent.setIssueLabels(lables);
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processUnLabled(childList,iList);
				}
			}
		}
		return iList;
	}
	
	/**
	 * 处理指派某人操作
	 * 
	 * 这里注意的是，author标签下是被指派的人，指派人干活的是后面的家伙
	 * 如果找不到指派人的那个node，则是开发者自己指派自己
	 * @param nodeList
	 * @param iList
	 * @return
	 */
	private List<IssuesEvent> processAssigned(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"discussion-item discussion-item-assigned\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("assigned");
				Node assignedNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setIssueContent(assignedNode.toPlainTextString());
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				if(actorNode!=null){
					issuesEvent.setActor(actorNode.toPlainTextString());
				}else{
					issuesEvent.setActor(assignedNode.toPlainTextString());
				}
				System.out.println(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processAssigned(childList,iList);
				}
			}
		}
		return iList;

	}
	
	/**
	 * 处理取消指派某人操作
	 * 
	 * 跟之前一样，取消指派的是后面的家伙
	 * @param nodeList
	 * @param iList
	 * @return
	 */
	private List<IssuesEvent> processUnassigned(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"discussion-item discussion-item-unassigned\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("assigned");
				Node assignedNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setIssueContent(assignedNode.toPlainTextString());
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				if(actorNode!=null){
					issuesEvent.setActor(actorNode.toPlainTextString());
				}else{
					issuesEvent.setActor(assignedNode.toPlainTextString());
				}
				System.out.println(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processUnassigned(childList,iList);
				}
			}
		}
		return iList;

	}
	
	/**
	 * 处理标记里程碑动作
	 * @param nodeList
	 * @param iList
	 * @return
	 */
	public List<IssuesEvent> processMileStone(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("div class=\"discussion-item discussion-item-milestoned\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("milestone");
				Node milestoneNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				//Pattern milestonePattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/.*+");
				Pattern milestonePattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/.*+");
				//Pattern milestonePattern = Pattern.compile("zurb/foundation-sites/[a-zA-Z]+/.*+");
				Matcher milestoneMatcher = milestonePattern.matcher(milestoneNode.getText());
				if(milestoneMatcher.find()){
					String milestone = milestoneMatcher.group().split("\"")[0];
					issuesEvent.setIssueContent(milestone);
				}
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processMileStone(childList,iList);
				}
			}
		}
		return iList;
	
	}
	
	/**
	 * 处理移除里程碑动作
	 * @param nodeList
	 * @param iList
	 * @return
	 */
	public List<IssuesEvent> processRemoveMileStone(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("div class=\"discussion-item discussion-item-demilestoned\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("removeMilestone");
				Node milestoneNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				Pattern milestonePattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/.*+");
			//	Pattern milestonePattern = Pattern.compile("zurb/foundation-sites/[a-zA-Z]+/.*+");

				Matcher milestoneMatcher = milestonePattern.matcher(milestoneNode.getText());
				if(milestoneMatcher.find()){
					String milestone = milestoneMatcher.group().split("\"")[0];
					issuesEvent.setIssueContent(milestone);
				}
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processRemoveMileStone(childList,iList);
				}
			}
		}
		return iList;
		
	
	}
	
	/**
	 * 处理reopenIssue的操作
	 * @param nodeList
	 * @param iList
	 * @return
	 */
	public List<IssuesEvent> processReopenIssue(NodeList nodeList,List<IssuesEvent> iList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("div class=\"discussion-item discussion-item-reopened\"")) {
				IssuesEvent issuesEvent = new IssuesEvent();
				issuesEvent.setIssueAction("reopen");
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					issuesEvent.setCreatedAt(time);
				}
				iList.add(issuesEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processReopenIssue(childList,iList);
				}
			}
		}
		return iList;
	}
	
	public String processArtifactId(String nodeList){
		String artifactId = "";
		
		Pattern artifactPattern = Pattern.compile("https://github.com/[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/[a-z[0-9]]+");
		//Pattern artifactPattern = Pattern.compile("https://github.com/zurb/foundation-sites/[a-zA-Z]+/[a-z[0-9]]+");

		Matcher matcher = artifactPattern.matcher(nodeList);
		if(matcher.find()){
			artifactId = matcher.group();
		}
		return artifactId;
	}
	
	/*------------------------主要功能方法------------------------*/
	/**
	 * 对外暴露的方法，用于获取url网页当中的数据，
	 * @param url
	 * @throws IOException 
	 */
	public void getDataFromIssueUrl(String url) throws IOException{
		/**
		 * 将数据填充到issueCommentEvent对象或IssuesEvent对象中，并用hibernate实现存储
		 */
		NodeList nodeList = null;
		String artifactId = ""; 
		try {
			String homePage = htmlAnalyzer.getResource(url);
			nodeList = htmlAnalyzer.getNodeListByHtmlPage(homePage);
			artifactId = processArtifactId(homePage);
			System.out.println(artifactId);
			if(!(artifactId).equals(url)){
				System.out.println("当前artifact已经转换为其他形式");
				return ;
			}
		} catch (Exception e) {
			System.out.println("出现异常");
			return ;
		}
		List<IssueCommentEvent> icList = new ArrayList<IssueCommentEvent>();
		List<IssuesEvent> iList = new ArrayList<IssuesEvent>();
		icList = this.processComment(nodeList,icList);
		iList = this.processOpenIssue(nodeList,iList);
		iList = this.processCloseIssue(nodeList, iList);
		iList = this.processReopenIssue(nodeList, iList);
		iList = this.processLabled(nodeList, iList);
		iList = this.processUnLabled(nodeList, iList);
		iList = this.processReference(nodeList, iList);
		iList = this.processAssigned(nodeList, iList);
		iList = this.processUnassigned(nodeList, iList);
		iList = this.processMileStone(nodeList, iList);
		iList = this.processRemoveMileStone(nodeList, iList);
		
		/**
		 * 解析所有对象中共有的信息，包括ArtifactId,网页URL，数据来源类型net,网页的repository
		 */
		
		
		for(IssueCommentEvent i:icList){
			i.setArtifactId(artifactId);
			i.setHtmlUrl(url);
			i.setSourceType("net");
			i.setRepo(getRepo());
		}
		for(IssuesEvent i:iList){
			i.setArtifactId(artifactId);
			i.setIssueUrl(url);
			i.setSourceType("net");
			i.setRepo(getRepo());
		}
		/**
		 * 向数据库中持久化数据
		 */
		session.beginTransaction();
		for(IssuesEvent i:iList){
			session.save(i);
			if(i.getIssueAction().equals("open")){
				ArtifactOwner artifactOwner = new ArtifactOwner();
				artifactOwner.setArtifactId(artifactId);
				artifactOwner.setOwner(i.getActor());
				session.save(artifactOwner);
			}
		}
		for(IssueCommentEvent ic:icList){
			session.save(ic);
		}
		
		session.getTransaction().commit();
		
	}
	
	
	/*------------------------工具方法------------------------*/
	
	
	public static void main(String args[]) throws IOException {
		HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
		IssueDataFromHtml issueDataFromHtml = new IssueDataFromHtml();
		issueDataFromHtml.setRepo("DefinitelyTyped/DefinitelyTyped/");
		Session session = HibernateUtil.getSessionFactory().openSession();
		issueDataFromHtml.setSession(session);
		//3648
		for(int i=2013;i<=8671;i++){
			try {
				String url = "https://github.com/DefinitelyTyped/DefinitelyTyped/issues/"+String.valueOf(i);
				System.out.println("解析第"+i+"个");
				issueDataFromHtml.getDataFromIssueUrl(url);
			} catch (Exception e) {
				/**
				 * 直接吃掉异常，进行下一个url的解析
				 */
				continue;
			}
		}
		HibernateUtil.closeSessionFactory();
	}
}
