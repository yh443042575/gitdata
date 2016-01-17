package edu.hit.yh.gitdata.githubDataDownload;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.hit.yh.gitdata.githubDataAnalyzer.HtmlAnalyzer;
import edu.hit.yh.gitdata.githubDataModel.CommitUrls;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;

public class CommitHtml {

	private static HtmlAnalyzer htmAnalyzer = new HtmlAnalyzer();
	
	/**
	 * 得到某个repository下所有的commit的Html
	 * pages是该repo下的commit页数，一页可以有多个commit
	 * @param repo
	 */
	public static void getUrls(String repo,String branch,int pages){
		List<CommitUrls> sList = new ArrayList<CommitUrls>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		for(int i=41;i<=pages;i++){
			System.out.println("解析第"+i+"个页面");
			session.beginTransaction();
			NodeList nodeList = htmAnalyzer.getNodeList("https://github.com/"+repo+"commits/"+branch+"?page="+i);
			processUrls(nodeList, sList, repo);
			for(CommitUrls c:sList){
				session.save(c);
			}
			session.getTransaction().commit();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		session.close();
		HibernateUtil.closeSessionFactory();
	}
	
	public static List<CommitUrls> processUrls(NodeList nodeList,List<CommitUrls> sList,String repo){
		SimpleNodeIterator snIterator = nodeList.elements();
		while (snIterator.hasMoreNodes()) {
			Node node = snIterator.nextNode();
			if(node.getText().contains("commit commits-list-item table-list-item")){
				CommitUrls commitUrls = new CommitUrls();
				Node urlNode = DownloadUtil.getSomeChild(node, "a href=\"/"+repo);
				Pattern pattern = Pattern.compile("/"+repo+"commit/[a-z[0-9]]+");
				Matcher matcher = pattern.matcher(urlNode.getText());
				if(matcher.find()){
					commitUrls.setUrl(matcher.group());
				}
				Pattern titlePattern = Pattern.compile("title=\".*\"");
				Matcher titleMatcher = titlePattern.matcher(urlNode.getText());
				if(titleMatcher.find()){
					commitUrls.setTitle(titleMatcher.group().split("\"")[1]);
				}
				commitUrls.setRepo(repo);
				sList.add(commitUrls);
			}else{
				// 得到该节点的子节点列表
				NodeList childList = node.getChildren();
				// 孩子节点为空，说明是值节点
				if (null != childList) {//如果孩子结点不为空则递归调用
					processUrls(childList,sList,repo);
				}
			}
		}
		return sList;
	}
	
	
	public static void main(String args[]){
		CommitHtml.getUrls("jquery/jquery/","master",170);
		
	}
}
