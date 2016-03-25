package edu.hit.yh.gitdata.mine.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;

import edu.hit.yh.gitdata.mine.module.AbstractActorAndRelativeTimeBehavior;
import edu.hit.yh.gitdata.mine.module.BehaviorPattern;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;
import edu.hit.yh.gitdata.mine.module.TimeBasedBehavior;

/**
 * 用来将GraphUtil图形化的工具，输入各种类型的行为，输出
 * @author DHAO
 *
 */
public class GraphUtil {
	/**
	 * 输入行为列表和存储路径
	 * @param behaviorList
	 * @param dir
	 * @throws IOException 
	 */
	public static void exportSimpleBehaviorGraph(List<BehaviorPattern> behaviorPatternList,String dir) throws IOException{
		//最终生成的图形dot代码存在sb中
		StringBuilder sb=new StringBuilder();
		for(int j=behaviorPatternList.size()-1;j>behaviorPatternList.size()-5;j--){
			BehaviorPattern<SimpleBehavior> pattern = behaviorPatternList.get(j);
			sb.delete( 0, sb.length() );
			sb.append("digraph G {");
			sb.append("rankdir = LR;");
			List<SimpleBehavior> simList = pattern.getBehaviorList();
			for(int i = 0;i<simList.size();i++){
				SimpleBehavior sim = simList.get(i);
				if(i!=simList.size()-1){//后面还有可连接的结点
					sb.append(i+"->"+(i+1)+";");
				}
				sb.append(i+"[label = \""+sim.getActor()+"\n"+sim.getEventType()+"\"];");
			}
			sb.append("}");
		
			GraphViz graphViz = new GraphViz();
			
			byte[] img = graphViz.getGraph(sb.toString(), "png");
			File out = new File("G:\\out-"+String.valueOf(behaviorPatternList.indexOf(pattern))+"." + "png");    // Windows
			if(!out.exists()){
				out.createNewFile();
			}
			graphViz.writeGraphToFile(img,out);
		}
		
	}
	
	public static void exportTimeBasedGraph(List<BehaviorPattern> behaviorPatternList,String dir) throws IOException{
		//最终生成的图形dot代码存在sb中
		StringBuilder sb=new StringBuilder();
		for(int j=behaviorPatternList.size()-1;j>behaviorPatternList.size()-5;j--){
			BehaviorPattern<TimeBasedBehavior> pattern = behaviorPatternList.get(j);
			sb.delete( 0, sb.length() );
			sb.append("digraph G {");
			sb.append("rankdir = LR;");
			List<TimeBasedBehavior> tbList = pattern.getBehaviorList();
			for(int i = 0;i<tbList.size();i++){
				TimeBasedBehavior sim = tbList.get(i);
				if(i!=tbList.size()-1){//后面还有可连接的结点
					sb.append(i+"->"+(i+1));
					sb.append("[label = \""+tbList.get(i+1).getRelativeTime()+"\"];");
				}
				sb.append(i+"[label = \""+sim.getActor()+"\n"+sim.getEventType()+"\"];");
			}
			sb.append("}");
		
			GraphViz graphViz = new GraphViz();
			
			byte[] img = graphViz.getGraph(sb.toString(), "png");
			File out = new File("G:\\tout-"+String.valueOf(behaviorPatternList.indexOf(pattern))+"." + "png");    // Windows
			if(!out.exists()){
				out.createNewFile();
			}
			graphViz.writeGraphToFile(img,out);
		}
		
	}
	
	
	
	public static void exportAbstractActorTimeBasedGraph(List<String> behaviorPatternList,String dir) throws IOException{
		//最终生成的图形dot代码存在sb中
		StringBuilder sb=new StringBuilder();
		for(int j=0;j<behaviorPatternList.size();j++){
			String pattern = behaviorPatternList.get(j);
			sb.delete( 0, sb.length() );
			sb.append("digraph G {");
			sb.append("rankdir = LR;");
			String[] tbList = pattern.split("\\|");
			for(int i = 0;i<tbList.length;i++){
				String[] aat = tbList[i].split("\\+");
				String actor = aat[0];
				String eventType = aat[1];
				
				
				if(i!=tbList.length-1){//后面还有可连接的结点,则取出下一个行为的相对时间
					String[] aat2 = tbList[i+1].split("\\+");
					String relativeTimeString = aat2[2];
					sb.append(i+"->"+(i+1));
					sb.append("[label = \""+relativeTimeString+"\"];");
				}
				sb.append(i+"[label = \"person"+actor+"\n"+eventType+"\"];");
			}
			sb.append("}");
			
			GraphViz graphViz = new GraphViz();
			
			byte[] img = graphViz.getGraph(sb.toString(), "png");
			File out = new File("G:\\aatout-"+String.valueOf(behaviorPatternList.indexOf(pattern))+"." + "png");    // Windows
			if(!out.exists()){
				out.createNewFile();
			}
			graphViz.writeGraphToFile(img,out);
		}
		
	}
	
	public static void exportAbstractActorGraph(List<String> behaviorPatternList,String dir) throws IOException{
		//最终生成的图形dot代码存在sb中
		StringBuilder sb=new StringBuilder();
		for(int j=0;j<behaviorPatternList.size();j++){
			String pattern = behaviorPatternList.get(j);
			sb.delete( 0, sb.length() );
			sb.append("digraph G {");
			sb.append("rankdir = LR;");
			String[] tbList = pattern.split("\\|");
			for(int i = 0;i<tbList.length;i++){
				String[] aat = tbList[i].split("\\+");
				String actor = aat[0];
				String eventType = aat[1];
				
				if(i!=tbList.length-1){//后面还有可连接的结点,则取出下一个行为的相对时间
					String[] aat2 = tbList[i+1].split("\\+");
					//String relativeTimeString = aat2[2];
					sb.append(i+"->"+(i+1)+";");
				}
				sb.append(i+"[label = \"person"+actor+"\n"+eventType+"\"];");
			}
			sb.append("}");
			
			GraphViz graphViz = new GraphViz();
			
			byte[] img = graphViz.getGraph(sb.toString(), "png");
			File out = new File(dir+"\\aatout-"+String.valueOf(behaviorPatternList.indexOf(pattern))+"." + "png");    // Windows
			if(!out.exists()){
				out.createNewFile();
			}
			graphViz.writeGraphToFile(img,out);
		}
		
	}
	
	
	public static void main(String args[]) throws IOException{
		/*List<BehaviorPattern<SimpleBehavior>> patternList =  new ArrayList<BehaviorPattern<SimpleBehavior>>();
		BehaviorPattern<SimpleBehavior> pattern1=new BehaviorPattern<SimpleBehavior>();
		List<SimpleBehavior> behaviors = new ArrayList<SimpleBehavior>();
		SimpleBehavior sim1 = new SimpleBehavior();
		sim1.setActor("yh");
		sim1.setEventType("issuecomment");
		SimpleBehavior sim2 = new SimpleBehavior();
		sim2.setActor("ltz");
		sim2.setEventType("openissue");
		behaviors.add(sim1);
		behaviors.add(sim2);
		pattern1.setBehaviorList(behaviors);
		patternList.add(pattern1);*/
		//GraphUtil.exportSimpleBehaviorGraph(patternList, "");
	
		//String test = "1+issueComment+0|3+addLable+less than 1|1+reference+less than 1|1+issueComment+less than 1|1+reference+between 3 and 5|4+issueComment+more than 14|7+reference+more than 14|";
		String test = "1+issueComment|3+addLable|1+reference|1+issueComment|1+reference|4+issueComment|7+reference|";

		List<String> aatList = new ArrayList<String>();
		aatList.add(test);
		GraphUtil.exportAbstractActorGraph(aatList, "");
		
		
		
	}
	
	
}
