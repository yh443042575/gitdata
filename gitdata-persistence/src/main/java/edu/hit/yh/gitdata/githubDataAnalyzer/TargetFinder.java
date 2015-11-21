package edu.hit.yh.gitdata.githubDataAnalyzer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * 寻找行为的接收者
 * 1、接受者可能直接来自用户的行为内容中，比如言论，或者assignee
 * 2、接收者可能也由Artifact产生
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
	
	String target;
	
	
	
	
	
	//通过用户的言论来找到行为的目标
	public String findTargetInArtifactByContent(String content){
		
		
		
		
		return null;
	}
	
	public String findTargetAmongArtifactsByUrl(String url) {
		
		
		Matcher matcher = PATTERN.matcher(url);
		//验证输入的字符串是否为url
		if(!matcher.find()){
			try {
				throw new IllegalAccessException();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}else{//如果url正确则寻找对应的fix Artifact
			HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
			System.out.println(url);
			
			NodeList nodeList = htmlAnalyzer.getNodeList(url);
			processUrlTargetNodeList(nodeList);
			
			return null;
		}
		
		
	}
	
	private void processUrlTargetNodeList(NodeList nodeList) {
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
					processUrlTargetNodeList(childList);
					// 若包含关键字，则简单打印出来文本
					// end if
				}	
			}
		}// end while
	}
	private String handleUrlTarget(Node node) {
		
		Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/(commit|issues|pull)/[a-z[0-9]]+");
		/*Pattern pattern = Pattern.compile("(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"   
           + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"   
           + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"   
           + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"   
           + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"   
           + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"   
           + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"   
           + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*");*/
		Matcher matcher = artifactPattern.matcher(node.getChildren().toString());
		while(matcher.find()){
			System.out.println(matcher.group());
		}
		
		return node.getChildren().elementAt(1).toPlainTextString().trim();
	}
}
