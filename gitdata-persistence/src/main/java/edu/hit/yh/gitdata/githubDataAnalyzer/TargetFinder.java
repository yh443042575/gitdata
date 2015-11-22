package edu.hit.yh.gitdata.githubDataAnalyzer;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;

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
	
	
	/**
	 * 通过开发者每一次行为的谈话内容，识别出该行为是针对谁的
	 * @param content
	 * @return
	 */
	public static String findTargetInArtifactByContent(String content){
		StringBuffer target = new StringBuffer("");
		Pattern pattern = Pattern.compile("@[a-zA-Z]+");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			target.append(matcher.group()+"+");
		}
		return target.toString();
		
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
}

/*这是一个非常牛B的网址的正则表达式
 * Pattern pattern = Pattern.compile("(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"   
   + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"   
   + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"   
   + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"   
   + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"   
   + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"   
   + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"   
   + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*");*/
