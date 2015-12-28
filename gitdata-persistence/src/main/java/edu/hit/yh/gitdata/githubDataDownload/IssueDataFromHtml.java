package edu.hit.yh.gitdata.githubDataDownload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.hit.yh.gitdata.githubDataAnalyzer.HtmlAnalyzer;
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
				Node titleNode = getSomeChild(node, "span class=\"js-issue-title\"");
				issuesEvent.setIssueContent(titleNode.toPlainTextString());
				Node actorNode = getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = getSomeChild(node, "datetime");
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
				//System.out.println(node.getText());
				Node actorNode = getSomeChild(node, "class=\"author\"");
				i.setActor(actorNode.toPlainTextString());
				Node contentNode = getSomeChild(node, "div class=\"comment-body");
				i.setCommentBody(contentNode.toPlainTextString());
				Node timeNode = getSomeChild(node, "datetime");
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
				Node anotherAtifactNode = getSomeChild(node, "class=\"title-link\"");
				issuesEvent.setIssueContent(anotherAtifactNode.toPlainTextString());
				Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/[a-z[0-9]]+");
				Matcher artifactMatcher = artifactPattern.matcher(anotherAtifactNode.getText());
				if(artifactMatcher.find()){
					String anotherAtifact = artifactMatcher.group();
					issuesEvent.setDesContent1(anotherAtifact);
					System.out.println(anotherAtifact);
				}
				Node actorNode = getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = getSomeChild(node, "datetime");
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
				Node actorNode = getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = getSomeChild(node, "datetime");
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
				Node lableNode = getSomeChild(node, "style=\"color:");
				issuesEvent.setIssueLabels(lableNode.toPlainTextString());
				Node actorNode = getSomeChild(node, "class=\"author\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = getSomeChild(node, "datetime");
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
	 * 处理指派某人（包含取消指派操作）操作
	 * 
	 * 这里注意的是，author标签下是被指派的人，指派人干活的是后面的家伙
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
				issuesEvent.setIssueAction("Assigned");
				Node assignedNode = getSomeChild(node, "class=\"author\"");
				issuesEvent.setIssueContent(assignedNode.toPlainTextString());
				Node actorNode = getSomeChild(node, "class=\"discussion-item-entity\"");
				issuesEvent.setActor(actorNode.toPlainTextString());
				System.out.println(actorNode.toPlainTextString());
				Node timeNode = getSomeChild(node, "datetime");
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
	 * 处理标记里程碑动作
	 * @param nodeList
	 * @param iList
	 * @return
	 */
	private List<IssuesEvent> processMileStone(NodeList nodeList,List<IssuesEvent> iList){
		return null;
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
		NodeList nodeList = htmlAnalyzer.getNodeList(url);
		List<IssueCommentEvent> icList = new ArrayList<IssueCommentEvent>();
		List<IssuesEvent> iList = new ArrayList<IssuesEvent>();
		icList = this.processComment(nodeList,icList);
		iList = this.processOpenIssue(nodeList,iList);
		iList = this.processCloseIssue(nodeList, iList);
		iList = this.processLabled(nodeList, iList);
		iList = this.processReference(nodeList, iList);
		iList = this.processAssigned(nodeList, iList);
		
		/**
		 * 解析所有对象中共有的信息，包括ArtifactId,网页URL，数据来源类型net,网页的repository
		 */
		String artifactId = null; 
		Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/issues/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(url);
		if(matcher.find()){
			artifactId = matcher.group();
		}
		for(IssueCommentEvent i:icList){
			i.setArtifactId(artifactId);
			i.setHtmlUrl(url);
			i.setSourceType("net");
			i.setRepo(getRepo());
		}
		
	}
	
	/*------------------------工具方法------------------------*/
	
	/**
	 * 迭代方式找到某个node下的满足我们需要的条件的子node
	 * @param root
	 * @param condition
	 * @return
	 */
	private Node getSomeChild(Node root,String condition){
		Node node = null;
		SimpleNodeIterator iterator = null;
		if(root.getChildren()!=null)
		iterator = root.getChildren().elements();
		while (iterator!=null&&iterator.hasMoreNodes()) {
			Node cNode = iterator.nextNode();
			if(cNode.getText().contains(condition)){
				return cNode;
			}else {
				if(cNode!=null){
					node = getSomeChild(cNode,condition);
				}
				if(node!=null){
					return node;
				}
			}
		}
		return null;
	}
	
	
	
	public static void main(String args[]) throws IOException{
		HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
		String url = "https://github.com/jquery/jquery/issues/2710";
		IssueDataFromHtml issueDataFromHtml = new IssueDataFromHtml();
		NodeList nodeList = htmlAnalyzer.getNodeList(url);
		issueDataFromHtml.processAssigned(nodeList, new ArrayList<IssuesEvent>());
		//issueDataFromHtml.getDataFromIssueUrl(url);
		
	}

}
