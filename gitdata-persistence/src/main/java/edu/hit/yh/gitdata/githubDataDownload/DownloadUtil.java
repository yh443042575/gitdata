package edu.hit.yh.gitdata.githubDataDownload;

import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * Download的工具类，用来解析HTML的各类节点
 * 
 * @author DHAO
 *
 */
public class DownloadUtil {

	/**
	 * 迭代方式找到某个node下的满足我们需要的条件的子node
	 * @param root
	 * @param condition
	 * @return
	 */
	public static Node getSomeChild(Node root,String condition){
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
	
	
	/**
	 * 如果开发者一次加入多个labels则以逗号分隔返回各个labels的结果
	 * @param root
	 * @param condition
	 * @param lableList
	 * @return
	 */
	public static List<Node> getLableList(Node root,String condition,List<Node> lableList){
		SimpleNodeIterator iterator = null;
		if(root.getChildren()!=null)
		iterator = root.getChildren().elements();
		while (iterator!=null&&iterator.hasMoreNodes()) {
			Node cNode = iterator.nextNode();
			if(cNode.getText().contains(condition)){
				lableList.add(cNode);
			}else {
				if(cNode!=null){
					lableList = getLableList(cNode,condition,lableList);
				}
			}
		}
		return lableList;
	}
	
}
