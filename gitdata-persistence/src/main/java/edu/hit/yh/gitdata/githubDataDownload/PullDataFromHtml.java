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
import edu.hit.yh.gitdata.githubDataModel.DeleteEvent;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.githubDataModel.IssueCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.IssuesEvent;
import edu.hit.yh.gitdata.githubDataModel.PullRequestEvent;
import edu.hit.yh.gitdata.githubDataModel.PullRequestReviewCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.PushEvent;

/**
 * 补全github_archive数据库中的数据
 * 
 * 大体思路： 1、先得到网页的源代码， 2、解析每一类行为
 * 
 * @author DHAO
 *
 */
public class PullDataFromHtml {

	private HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();

	@Getter
	@Setter
	private Session session ;
	/**
	 * 要解析的repository
	 */
	@Getter
	@Setter
	private String repo;
	
	/**
	 * 处理开启pullrequest的需求
	 * 
	 * @param nodeList
	 * @param pList
	 * @return
	 */
	public List<PullRequestEvent> processOpenPull(NodeList nodeList,
			List<PullRequestEvent> pList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains("div id=\"issue-")) {
				PullRequestEvent pullRequestEvent = new PullRequestEvent();
				pullRequestEvent.setAction("open");
				Node commentNode = DownloadUtil.getSomeChild(node,
						"div class=\"comment-body");
				pullRequestEvent.setBody(commentNode.toPlainTextString());
				Node actorNode = DownloadUtil.getSomeChild(node,
						"class=\"author");
				pullRequestEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					pullRequestEvent.setCreatedAt(time);
				}
				pList.add(pullRequestEvent);

			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processOpenPull(childList, pList);
				}
			}
		}
		return pList;
	}

	/**
	 * 提取网页中所有的IssueComment元素
	 * 
	 * @param source
	 */
	private List<IssueCommentEvent> processComment(NodeList nodeList,
			List<IssueCommentEvent> icList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().matches("div id=\"issuecomment-.*\".*+")) {
				IssueCommentEvent i = new IssueCommentEvent();
				// TODO 解析comment工作
				Node actorNode = DownloadUtil.getSomeChild(node,
						"class=\"author\"");
				i.setActor(actorNode.toPlainTextString());
				Node contentNode = DownloadUtil.getSomeChild(node,
						"div class=\"comment-body");
				i.setCommentBody(contentNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					i.setCreatedAt(time);
					System.out.println(time);
				}
				icList.add(i);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processComment(childList, icList);
				}
			}
		}
		return icList;
	}

	/**
	 * 处理labeled操作
	 * 
	 * @param source
	 */
	public List<PullRequestEvent> processLabled(NodeList nodeList,
			List<PullRequestEvent> pList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains(
					"class=\"discussion-item discussion-item-labeled\"")) {
				PullRequestEvent pullRequestEvent = new PullRequestEvent();
				pullRequestEvent.setAction("labeled");
				List<Node> lableList = new ArrayList<Node>();
				lableList = DownloadUtil.getLableList(node, "style=\"color:",
						lableList);
				String lables = "";
				for (int i = 0; i < lableList.size(); i++) {
					lables += lableList.get(i).toPlainTextString();
					if (i != lableList.size() - 1) {
						lables += ",";
					}
				}
				System.out.println(lables);
				pullRequestEvent.setPullrequestBaseLabels(lables);
				Node actorNode = DownloadUtil.getSomeChild(node,
						"class=\"author\"");
				pullRequestEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					pullRequestEvent.setCreatedAt(time);
				}
				pList.add(pullRequestEvent);

			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processLabled(childList, pList);
				}
			}
		}
		return pList;
	}
	
	/**
	 * 处理unlabeled
	 */
	public List<PullRequestEvent> processUnLabled(NodeList nodeList,List<PullRequestEvent> pList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"discussion-item discussion-item-unlabeled")) {
				PullRequestEvent p = new PullRequestEvent();
				p.setAction("unlabeled");
				List<Node> lableList = new ArrayList<Node>(); 
				lableList= DownloadUtil.getLableList(node, "style=\"color:", lableList);
				String lables = "";
				for(int i=0;i<lableList.size();i++){
					lables+=lableList.get(i).toPlainTextString();
					if(i!=lableList.size()-1){
						lables+=",";
					}
				}
				p.setBody(lables);
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				p.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					p.setCreatedAt(time);
				}
				pList.add(p);
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processUnLabled(childList,pList);
				}
			}
		}
		return pList;
	}

	/**
	 * 处理对pullrequest的review时，comment的操作, 与processSubPullRequestReviewComment配合一起使用
	 * 
	 * @param nodeList
	 * @param prList
	 * @return
	 */
	public List<PullRequestReviewCommentEvent> processPullRequestReviewComment(
			NodeList nodeList, List<PullRequestReviewCommentEvent> prList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains("div id=\"diff-for-comment-")) {
				String discussionId = node.getText().split("\"")[1];
				System.out.println(discussionId);
				NodeList subNodeList = node.getChildren();
				prList = processSubPullRequestReviewComment(subNodeList,
						prList, discussionId);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processPullRequestReviewComment(childList, prList);
				}
			}
		}
		return prList;
	}

	public List<PullRequestReviewCommentEvent> processSubPullRequestReviewComment(
			NodeList nodeList, List<PullRequestReviewCommentEvent> prList,
			String discussionId) {
		SimpleNodeIterator sni2 = nodeList.elements();
		while (sni2.hasMoreNodes()) {
			Node node2 = sni2.nextNode();
			if (node2.getText().contains("div id=\"discussion_r")) {
				PullRequestReviewCommentEvent p = new PullRequestReviewCommentEvent();
				// TODO 解析comment工作
				p.setDiscussionId(discussionId);
				
				Node actorNode = DownloadUtil.getSomeChild(node2,
						"class=\"author\"");
				p.setActor(actorNode.toPlainTextString());
				System.out.println(actorNode.toPlainTextString());
				Node contentNode = DownloadUtil.getSomeChild(node2,
						"div class=\"comment-body");
				p.setCommentBody(contentNode.toPlainTextString());
				System.out.println(contentNode.toPlainTextString().trim());
				Node timeNode = DownloadUtil.getSomeChild(node2, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					p.setCreatedAt(time);
				}
				prList.add(p);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node2.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processSubPullRequestReviewComment(childList, prList,discussionId);
				}
			}
		}
		return prList;
	}

	/**
	 * 提取网页中的删除操作
	 * 
	 * @param nodeList
	 * @param dList
	 * @return
	 */
	public List<DeleteEvent> processDelete(NodeList nodeList,
			List<DeleteEvent> dList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains("discussion-item-head_ref_deleted")) {
				DeleteEvent d = new DeleteEvent();
				// TODO 解析comment工作
				Node deleteNode = DownloadUtil.getSomeChild(node,
						"span title=\"");
				d.setRef(deleteNode.getText().split("\"")[1]);
				System.out.println(deleteNode.getText().split("\"")[1]);
				Node actorNode = DownloadUtil.getSomeChild(node,
						"class=\"author\"");
				d.setActor(actorNode.toPlainTextString());
				System.out.println(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					d.setDeleteAt(time);
				}
				dList.add(d);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processDelete(childList, dList);
				}
			}
		}

		return dList;
	}

	/**
	 * 处理关闭pull操作
	 * 
	 * @param nodeList
	 * @param dList
	 * @return
	 */
	public List<PullRequestEvent> processClosePull(NodeList nodeList,
			List<PullRequestEvent> pList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains(
					"div class=\"discussion-item discussion-item-closed\"")) {
				PullRequestEvent pullRequestEvent = new PullRequestEvent();
				pullRequestEvent.setAction("close");
				Node actorNode = DownloadUtil.getSomeChild(node,
						"class=\"author\"");
				pullRequestEvent.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					pullRequestEvent.setCreatedAt(time);
				}
				pList.add(pullRequestEvent);

			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processClosePull(childList, pList);
				}
			}
		}

		return pList;
	}

	/**
	 * 处理Reference了当前pullrequest的操作
	 * 
	 * @param source
	 */
	public List<PullRequestEvent> processReference(NodeList nodeList,
			List<PullRequestEvent> pList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains(
					"div class=\"discussion-item discussion-item-ref\"")) {
				PullRequestEvent pullRequestEvent = new PullRequestEvent();
				pullRequestEvent.setAction("ref");
				Node anotherAtifactNode = DownloadUtil.getSomeChild(node,
						"class=\"title-link\"");
				pullRequestEvent
						.setBody(anotherAtifactNode == null?"":anotherAtifactNode.toPlainTextString());
				Pattern artifactPattern = Pattern
						.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/[a-z[0-9]]+");
				Matcher artifactMatcher = artifactPattern
						.matcher(anotherAtifactNode == null?"":anotherAtifactNode.getText());
				if (artifactMatcher.find()) {
					String anotherAtifact = artifactMatcher.group();
					pullRequestEvent.setPullrequestBaseRef(anotherAtifact);
					System.out.println(anotherAtifact);
				}
				Node actorNode = DownloadUtil.getSomeChild(node,
						"class=\"author\"");
				pullRequestEvent.setActor(actorNode==null?"":actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					pullRequestEvent.setCreatedAt(time);
				}
				pList.add(pullRequestEvent);

			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processReference(childList, pList);
				}
			}
		}
		return pList;
	}

	/**
	 * 处理标记里程碑动作
	 * @param nodeList
	 * @param pList
	 * @return
	 */
	public List<PullRequestEvent> processMileStone(NodeList nodeList,List<PullRequestEvent> pList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("div class=\"discussion-item discussion-item-milestoned\"")) {
				PullRequestEvent p = new PullRequestEvent();
				p.setAction("milestone");
				Node milestoneNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				Pattern milestonePattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/.*+");
				Matcher milestoneMatcher = milestonePattern.matcher(milestoneNode.getText());
				if(milestoneMatcher.find()){
					String milestone = milestoneMatcher.group().split("\"")[0];
					p.setBody(milestone);
				}
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				p.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					p.setCreatedAt(time);
				}
				pList.add(p);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processMileStone(childList,pList);
				}
			}
		}
		return pList;
	}
	
	/**
	 * 处理移除里程碑动作
	 * @param nodeList
	 * @param pList
	 * @return
	 */
	public List<PullRequestEvent> processRemoveMileStone(NodeList nodeList,List<PullRequestEvent> pList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("div class=\"discussion-item discussion-item-demilestoned\"")) {
				PullRequestEvent p = new PullRequestEvent();
				p.setAction("removeMilestone");
				Node milestoneNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				Pattern milestonePattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/.*+");
				Matcher milestoneMatcher = milestonePattern.matcher(milestoneNode.getText());
				if(milestoneMatcher.find()){
					String milestone = milestoneMatcher.group().split("\"")[0];
					p.setBody(milestone);
				}
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				p.setActor(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					p.setCreatedAt(time);
				}
				pList.add(p);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processRemoveMileStone(childList,pList);
				}
			}
		}
		return pList;
		
	
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
	private List<PullRequestEvent> processAssigned(NodeList nodeList,List<PullRequestEvent> pList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"discussion-item discussion-item-assigned\"")) {
				PullRequestEvent pEvent = new PullRequestEvent();
				pEvent.setAction("assigned");
				Node assignedNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				pEvent.setPullrequestAssgnee(assignedNode==null?"":assignedNode.toPlainTextString());
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				if(actorNode!=null){
					pEvent.setActor(actorNode.toPlainTextString());
				}else{
					pEvent.setActor(assignedNode.toPlainTextString());
				}
				System.out.println(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					pEvent.setCreatedAt(time);
				}
				pList.add(pEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processAssigned(childList,pList);
				}
			}
		}
		return pList;

	}
	
	/**
	 * 处理取消指派某人操作
	 * 
	 * 跟之前一样，取消指派的是后面的家伙
	 * @param nodeList
	 * @param pList
	 * @return
	 */
	private List<PullRequestEvent> processUnassigned(NodeList nodeList,List<PullRequestEvent> pList){
		SimpleNodeIterator sni = nodeList.elements();
		while(sni.hasMoreNodes()){
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"discussion-item discussion-item-unassigned\"")) {
				PullRequestEvent pEvent = new PullRequestEvent();
				pEvent.setAction("assigned");
				Node assignedNode = DownloadUtil.getSomeChild(node, "class=\"author\"");
				pEvent.setPullrequestAssgnee(assignedNode.toPlainTextString());
				Node actorNode = DownloadUtil.getSomeChild(node, "class=\"discussion-item-entity\"");
				if(actorNode!=null){
					pEvent.setActor(actorNode.toPlainTextString());
				}else{
					pEvent.setActor(assignedNode.toPlainTextString());
				}
				System.out.println(actorNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if(matcher.find()){
					String time = matcher.group().split("\"")[1];
					pEvent.setCreatedAt(time);
				}
				pList.add(pEvent);
				
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processUnassigned(childList,pList);
				}
			}
		}
		return pList;

	}
	
	public String processArtifactId(String nodeList){
		String artifactId = "";
		Pattern artifactPattern = Pattern.compile("https://github.com/[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(nodeList);
		if(matcher.find()){
			artifactId = matcher.group();
		}
		return artifactId;
	}
	
	
	public void getDataFromPullUrl(String url) {

		/**
		 * 将数据填充到issueCommentEvent对象或IssuesEvent对象中，并用hibernate实现存储
		 * 
		 */
		NodeList nodeList = null;
		String artifactId = ""; 
		try {
			String homePage = htmlAnalyzer.getResource(url);
			nodeList = htmlAnalyzer.getNodeListByHtmlPage(homePage);
			artifactId = processArtifactId(homePage);
			if(!(artifactId).equals(url)){
				System.out.println("artifact 已经转换为其他形式");
				return ;
			}
		} catch (Exception e) {
			System.out.println("出现异常");
			return ;
		}
		
		List<PullRequestEvent> pList = new ArrayList<PullRequestEvent>();
		List<IssueCommentEvent> icList = new ArrayList<IssueCommentEvent>();
		List<PullRequestReviewCommentEvent> prList = new ArrayList<PullRequestReviewCommentEvent>();
		List<PushEvent> psList = new ArrayList<PushEvent>();
		/**
		 * 具体处理流程,一个完整的pullrequest大概包括： 1、开启一个pullrequest请求；
		 * 2、代码的拥有者或协作者对代码进行评论； 3、代码的拥有者对代码进行codeReview并对某一段代码进行评论
		 * 4、代码的提交者对代码进行修改 5、代码被主干的拥有者认可，最后将代码merge(push)到主干上
		 */
		pList = this.processOpenPull(nodeList, pList);
		pList = this.processClosePull(nodeList, pList);
		pList = this.processMileStone(nodeList, pList);
		pList = this.processRemoveMileStone(nodeList, pList);
		pList = this.processReference(nodeList, pList);
		pList = this.processLabled(nodeList, pList);
		pList = this.processUnLabled(nodeList, pList);
		icList = this.processComment(nodeList, icList);
		/*prList = this.processPullRequestReviewComment(nodeList, prList);*/
		pList = this.processAssigned(nodeList, pList);
		pList = this.processUnassigned(nodeList, pList);
		/**
		 * 解析所有对象中共有的信息，包括ArtifactId,网页URL，数据来源类型net,网页的repository
		 */
		for(IssueCommentEvent i:icList){
			i.setArtifactId(artifactId);
			i.setHtmlUrl(url);
			i.setSourceType("net");
			i.setRepo(getRepo());
		}
		for(PullRequestEvent p:pList){
			p.setArtifactId(artifactId);
			p.setPullrequestHtmlUrl(url);
			p.setSourceType("net");
			p.setRepo(getRepo());
		}
		for(PullRequestReviewCommentEvent pr:prList){
			pr.setArtifactId(artifactId);
			pr.setHtmlUrl(url);
			pr.setSourceType("net");
			pr.setRepo(getRepo());
		}
		for(PushEvent ps:psList){
			ps.setArtifactId(artifactId);
			ps.setHtmlUrl(url);
			ps.setSourceType("net");
			ps.setRepo(getRepo());
		}
		
		/**
		 * 向数据库中持久化数据
		 */
		session.beginTransaction();
		for(IssueCommentEvent ic:icList){
			session.save(ic);
		}
		for(PullRequestEvent p:pList){
			session.save(p);
			if(p.getAction().equals("open")){
				ArtifactOwner artifactOwner = new ArtifactOwner();
				artifactOwner.setArtifactId(artifactId);
				artifactOwner.setOwner(p.getActor());
				session.save(artifactOwner);
			}
		}
		for(PullRequestReviewCommentEvent pr:prList){
			session.save(pr);
		}
		for(PushEvent ps:psList){
			session.save(ps);
		}
		session.getTransaction().commit();
		
	}

	public static void main(String args[]) throws IOException {
		HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
		PullDataFromHtml pullDataFromHtml = new PullDataFromHtml();
		pullDataFromHtml.setRepo("jquery/jquery/");
		Session session = HibernateUtil.getSessionFactory().openSession();
		pullDataFromHtml.setSession(session);
		for(int i=1740;i<=2700;i++){
			try {
				String url = "https://github.com/jquery/jquery/pull/"+String.valueOf(i);
				System.out.println("解析第"+i+"个");
				pullDataFromHtml.getDataFromPullUrl(url);
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
