package edu.hit.yh.gitdata.mine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import edu.hit.yh.gitdata.mine.algorithm.SimpleGspMiningAlgorithm;
import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

/**
 * 统计工具，用来统计各个项目的规模
 * @author DHAO
 *
 */
public class StatisticsUtil {

	/**
	 * 统计协作行为的长度以及各自存在的数量
	 * @param repo
	 * @param artifactType
	 */
	public static void getBehaviorLengthAndCount(String repo,String artifactType ){
		SimpleGspMiningAlgorithm sgm = new SimpleGspMiningAlgorithm(0);
		List<Artifact<SimpleBehavior>> artifactList = sgm.buildArtifacts(repo, artifactType);
		HashMap<Integer, Integer> behaviorLengthAndCountMap = new HashMap<Integer, Integer>();
		for(Artifact<SimpleBehavior> artifact:artifactList){
			Integer bl = artifact.getBehaviorSeq().size();
			if(behaviorLengthAndCountMap.containsKey(bl)){
				Integer count = behaviorLengthAndCountMap.get(bl);
				count = count+1;
				behaviorLengthAndCountMap.replace(bl, count);
			}else {
				behaviorLengthAndCountMap.put(bl, 1);
			}
		}
		for(Map.Entry<Integer, Integer> entry:behaviorLengthAndCountMap.entrySet()){
			System.out.println(entry.getKey());
			
		}
		System.out.println("-----------");
		for(Map.Entry<Integer, Integer> entry:behaviorLengthAndCountMap.entrySet()){
			System.out.println(entry.getValue());
		}
	}
	
	/**
	 * 统计每次协作的人数与协作的数量
	 * @param repo
	 * @param artifactType
	 */
	public static void getPeopleAndCount(String repo,String artifactType){
		SimpleGspMiningAlgorithm sgm = new SimpleGspMiningAlgorithm(0);
		List<Artifact<SimpleBehavior>> artifactList = sgm.buildArtifacts(repo, artifactType);
		HashMap<Integer, Integer> behaviorLengthAndCountMap = new HashMap<Integer, Integer>();
		for(Artifact<SimpleBehavior> artifact:artifactList){
			List<String> actors = new ArrayList<String>();
			for(SimpleBehavior sb:artifact.getBehaviorSeq()){
				actors.add(sb.getActor());
			}
			List<String> actorWithoutDup = new ArrayList<String>(new HashSet<String>(actors));
			
			Integer p = actorWithoutDup.size();
			if(behaviorLengthAndCountMap.containsKey(p)){
				Integer count = behaviorLengthAndCountMap.get(p);
				count = count+1;
				behaviorLengthAndCountMap.replace(p, count);
			}else {
				behaviorLengthAndCountMap.put(p, 1);
			}
		}
		for(Map.Entry<Integer, Integer> entry:behaviorLengthAndCountMap.entrySet()){
			System.out.println(entry.getKey());
			
		}
		System.out.println("-----------");
		for(Map.Entry<Integer, Integer> entry:behaviorLengthAndCountMap.entrySet()){
			System.out.println(entry.getValue());
		}
	}
	
	
	public static void main(String args[]){
		//StatisticsUtil.getBehaviorLengthAndCount("zurb/foundation-sites/","Issue");
		StatisticsUtil.getPeopleAndCount("zurb/foundation-sites/","Issue");
	}
	
}
