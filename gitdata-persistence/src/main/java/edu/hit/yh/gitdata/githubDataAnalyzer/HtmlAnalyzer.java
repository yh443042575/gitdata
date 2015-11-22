package edu.hit.yh.gitdata.githubDataAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.visitors.HtmlPage;

/**
 * 当程序无法从githubArchive上获取到更详细的信息时，需要通过该程序来进行网页信息抓取，分析数据
 * 
 * @author DHAO
 *
 */
public class HtmlAnalyzer {

	String result = "";
	String htmlPage = "";
	NodeList htmlNodeList = null;

	/**
	 * 静态获取CommitCommentEvent中评论的内容
	 * 
	 * @param Url
	 * @param commentid
	 * @return
	 */
	public String getCommitCommentEventBodyByUrlAndCommentId(String url,
			String commentid) {

		NodeList nodeList = this.getNodeList(url);

		if (!nodeList.equals(null))
			processCommentBodyNodeList(nodeList, commentid);
		return result;
	}

	public String getCommitCommentEventCommitIdByUrl(String url) {
		NodeList nodeList = this.getNodeList(url);
		if (!nodeList.equals(null))
			processCommitIdNodeList(nodeList);

		return result;
	}

	/**
	 * 
	 * 静态从网页中获取issueCommentEvent中评论的内容
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String getIssueCommentEventBodyByUrlAndCommentId(String url,
			String commentid) {
		NodeList nodeList = this.getNodeList(url);

		if (nodeList != null)
			processIssueCommentBodyNodeList(nodeList, commentid);
		return result;
	}

	/* 负责向网站发送请求，得到解析的字符串 */
	public String getResource(String url) throws IOException {
		StringBuilder result = new StringBuilder("");
		String temp;

		URL getUrl = new URL(url);

		HttpsURLConnection httpURLConnection = (HttpsURLConnection) getUrl
				.openConnection();
		httpURLConnection.setConnectTimeout(600000);

		try {
			httpURLConnection.connect();
		} catch (java.net.ConnectException e) {
			System.err.println("------------超时的URL-----------------：" + url);
		}catch (java.net.SocketException e) {
			System.err.println("------------读取失败-----------------：" + url);
		}

		if (httpURLConnection.getResponseCode() != 404) {// 检测response状态码
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpURLConnection.getInputStream()));

			while ((temp = reader.readLine()) != null) {
				result.append(temp);
			}
			reader.close();
		} else {
			return null;
		}
		
		return result.toString();
	}

	/* 遍历结点，找到我们需要的包含讨论内容的结点 */
	private void processCommentBodyNodeList(NodeList list, String commentid) {
		// 迭代开始
		SimpleNodeIterator iterator = list.elements();
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			// System.out.println(node.getText());
			if (node.getText().matches(
					"div id=\"commitcomment-" + commentid + "\".*+")) {// add
				result = handleCommitCommentBody(node);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();

				// 孩子节点为空，说明是值节点
				if (null != childList) {
					processCommentBodyNodeList(childList, commentid);
					// 若包含关键字，则简单打印出来文本
					// end if
				}
			}
		}// end while
	}

	/* 遍历结点，找到我们需要的issueComment所包含的讨论内容的结点 */
	private void processIssueCommentBodyNodeList(NodeList list, String commentid) {
		// 迭代开始
		SimpleNodeIterator iterator = list.elements();
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			// System.out.println(node.getText());
			if (node.getText().matches(
					"div id=\"issuecomment-" + commentid + "\".*+")) {// add
				result = handleCommitCommentBody(node);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();

				// 孩子节点为空，说明是值节点
				if (null != childList) {
					processIssueCommentBodyNodeList(childList, commentid);
					// 若包含关键字，则简单打印出来文本
					// end if
				}
			}
		}// end while

	}

	private void processCommitIdNodeList(NodeList nodeList) {
		SimpleNodeIterator iterator = nodeList.elements();
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			// System.out.println(node.getText());
			if (node.getText().matches("div class=\"commit-meta clearfix\".*+")) {// add
				result = handleCommitId(node);
			} else {
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();

				// 孩子节点为空，说明是值节点
				if (null != childList) {
					processCommitIdNodeList(childList);
					// 若包含关键字，则简单打印出来文本
					// end if
				}
			}
		}// end while
	}

	private String handleCommitId(Node node) {
		return node.getChildren().elementAt(1).toPlainTextString().trim();
	}

	/* 得到对commit的comment的body，elementAt为其在网页中固有的顺序 */
	private String handleCommitCommentBody(Node node) {
		return node.getChildren().elementAt(3).getChildren().elementAt(1)
				.getChildren().elementAt(1).toPlainTextString().trim();
	}

	/**
	 * 若系统未下载页面，则通过网络下载页面并得到
	 * 
	 * @param url
	 * @return
	 */
	@SuppressWarnings("finally")
	public NodeList getNodeList(String url) {

		try {
			if (htmlPage.equals("")) {

				htmlPage = getResource(url);

				if (htmlPage != null) {
					Parser htmlParser = new Parser(htmlPage);

					HtmlPage visitor = new HtmlPage(htmlParser);

					htmlParser.visitAllNodesWith(visitor);

					htmlNodeList = visitor.getBody();

				} else {
					return null;
				}

			} else {
				result = "404 not found Exception";
			}

		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return htmlNodeList;
		}
	}

}
