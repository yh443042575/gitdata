package edu.hit.yh.gitdata.githubDataDownload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.Query;
import org.hibernate.Session;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.hit.yh.gitdata.githubDataAnalyzer.HtmlAnalyzer;
import edu.hit.yh.gitdata.githubDataModel.ArtifactOwner;
import edu.hit.yh.gitdata.githubDataModel.CommitCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.CommitUrls;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.githubDataModel.IssueCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.IssuesEvent;
import edu.hit.yh.gitdata.githubDataModel.PushEvent;

public class CommitDataFromHtml {

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
	public List<PushEvent> processPush(NodeList nodeList, List<PushEvent> pList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains("class=\"commit full-commit")) {
				PushEvent p = new PushEvent();
				// TODO 解析comment工作
				Node actorNode = DownloadUtil.getSomeChild(node,
						"rel=\"contributor\"");
				p.setActor(actorNode==null?"":actorNode.toPlainTextString());
				Node shaNode = DownloadUtil.getSomeChild(node, "class=\"sha ");
				p.setHead(shaNode.toPlainTextString());
				List<Node> artifactList = new ArrayList<Node>();
				artifactList = DownloadUtil.getLableList(node,
						"class=\"issue-link", artifactList);
				Pattern milestonePattern = Pattern
						.compile("href=\"https://github.com/[a-zA-Z]+/[a-zA-Z]+/[a-zA-Z]+/[0-9]+");
				String title = "";
				for (Node n : artifactList) {
					Matcher matcher = milestonePattern.matcher(n.getText());
					if (matcher.find()) {
						title += matcher.group().split("github.com/")[1] + " ";
					}
				}
				p.setTitle(title);
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					p.setCreatedAt(time);
					System.out.println(time);
				}
				pList.add(p);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processPush(childList, pList);
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
	private List<CommitCommentEvent> processComment(NodeList nodeList,
			List<CommitCommentEvent> ccList) {
		SimpleNodeIterator sni = nodeList.elements();
		while (sni.hasMoreNodes()) {
			Node node = sni.nextNode();
			if (node.getText().contains("div id=\"commitcomment")) {
				CommitCommentEvent i = new CommitCommentEvent();
				// TODO 解析comment工作
				Node actorNode = DownloadUtil.getSomeChild(node,
						"class=\"author\"");
				i.setActor(actorNode.toPlainTextString());
				Node contentNode = DownloadUtil.getSomeChild(node,
						"div class=\"comment-body");
				i.setBody(contentNode.toPlainTextString());
				System.out.println(contentNode.toPlainTextString());
				Node timeNode = DownloadUtil.getSomeChild(node, "datetime");
				Pattern pattern = Pattern.compile("datetime=\".*\"");
				Matcher matcher = pattern.matcher(timeNode.getText());
				if (matcher.find()) {
					String time = matcher.group().split("\"")[1];
					i.setCreatedAt(time);
				}
				ccList.add(i);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {// 如果孩子结点不为空则递归调用
					processComment(childList, ccList);
				}
			}
		}
		return ccList;
	}

	/*------------------------主要功能方法------------------------*/
	/**
	 * 对外暴露的方法，用于获取url网页当中的数据，
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void getDataFromCommitUrl(String url) {
		/**
		 * 将数据填充到issueCommentEvent对象或IssuesEvent对象中，并用hibernate实现存储
		 */
		NodeList nodeList = htmlAnalyzer.getNodeList(url);
		List<PushEvent> pList = new ArrayList<PushEvent>();
		List<CommitCommentEvent> ccList = new ArrayList<CommitCommentEvent>();
		pList = this.processPush(nodeList, pList);
		ccList = this.processComment(nodeList, ccList);

		/**
		 * 解析所有对象中共有的信息，包括ArtifactId,网页URL，数据来源类型net,网页的repository
		 */
		String artifactId = null;
		Pattern artifactPattern = Pattern
				.compile("[a-zA-Z]+/[a-zA-Z]+/commit/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(url);
		if (matcher.find()) {
			artifactId = matcher.group();
		}
		System.out.println(artifactId);
		for (PushEvent p : pList) {
			p.setArtifactId(artifactId);
			p.setHtmlUrl(url);
			p.setSourceType("net");
			p.setRepo(getRepo());
		}
		for (CommitCommentEvent cc : ccList) {
			cc.setArtifactId(artifactId);
			cc.setHtmlUrl(url);
			cc.setSourceType("net");
			cc.setRepo(getRepo());
		}
		/**
		 * 向数据库中持久化数据
		 */
		session.beginTransaction();
		for (PushEvent p : pList) {
			session.save(p);
			ArtifactOwner artifactOwner = new ArtifactOwner();
			artifactOwner.setArtifactId(artifactId);
			artifactOwner.setOwner(p.getActor());
			session.save(artifactOwner);
		}
		for (CommitCommentEvent cc : ccList) {
			session.save(cc);
		}
		session.getTransaction().commit();

	}

	public static void main(String args[]) {
		HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
		// String url =
		// "https://github.com/jquery/jquery/commit/56bb677725b21415905e5c3eeb1e05be4480e780";

		/*
		 * CommitDataFromHtml commitDataFromHtml = new CommitDataFromHtml();
		 * commitDataFromHtml.getDataFromCommitUrl(url);
		 */

		Session session = HibernateUtil.getSessionFactory().openSession();
		CommitDataFromHtml commitDataFromHtml = new CommitDataFromHtml();
		commitDataFromHtml.setSession(session);
		commitDataFromHtml.setRepo("query/query/");
		Query query = session.createSQLQuery("select * from commiturls")
				.addEntity(CommitUrls.class);
		List<CommitUrls> com = (List<CommitUrls>) query.list();
		for (int i = 4849; i < com.size(); i++) {
			System.out.println("解析第" + i + "个");
			String url = "https://github.com" + com.get(i).getUrl();
			commitDataFromHtml.getDataFromCommitUrl(url);
		}
		HibernateUtil.closeSessionFactory();

	}

}
