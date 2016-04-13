package edu.hit.yh.gitdata.analyseSR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.jpa.criteria.expression.function.AggregationFunction.COUNT;

import edu.hit.yh.gitdata.analyse.constant.AnalyseConstant;
import edu.hit.yh.gitdata.grphmodel.ShannonWiener;
import edu.hit.yh.gitdata.mine.constant.DirConstant;

/**
 * 通过统计工具，输出各类图形所需要的数据集
 * @author DHAO
 *
 */
public class GraphData {
	
	public static List<Integer> artifactListSize =  new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
	
	/**
	 * 计算每个模式下的丰富度模式丰富度的数据
	 * 通过获取本地文件，解析成图表所需要的文件
	 * @return
	 */
	public List<ShannonWiener> shannonWienerGraph(String algorithm,String repo){	
		
		String filePath = DirConstant.PATTERN_RESULT_FOLDER+algorithm+"-"+repo+"-";
		List<ShannonWiener> swList = new ArrayList<ShannonWiener>();
		for(int i=1;i<=5;i+=1){
			File file = new File(filePath+String.valueOf(i)+"%.txt");
			List<String> patterns = new ArrayList<String>();
			if(file.exists()){
				try {
					File patternFolder = new File(DirConstant.PATTERN_RESULT_FOLDER);
					String files[] = patternFolder.list();
					
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					//用于临时存放模式的String
					String ptn;
					//用于计算所有模式出现的总数
					Double total = 0.0;
					while((ptn=br.readLine())!=null){
						patterns.add(ptn);
						Double patternSurport = Double.valueOf(ptn.split("->")[1]);
						total += patternSurport;
					}
					Double swNum = CalculateUtil.getShannonWiener(patterns, total);
					ShannonWiener sw = new ShannonWiener();
					sw.setDiversity(swNum);
					sw.setSurpport(i);
					System.out.println(swNum+" "+i);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}
	/*
	 * 计算每个项目的具有个性化的模式
	 * 输出一个Map key是项目名称，value是这个项目里所具有的个性化模式的总量
	 */
	public HashMap<String, Double> individuationSequenceGraph() {
		
		HashMap<String, HashMap<String, Double>> repoProbabilityMap = CalculateUtil.getIndividuationSequence();
		HashMap<String, Double> repoIndividuationSequenceMap = new HashMap<String, Double>();
		
		List<String> repoList = new ArrayList<String>();
		List<HashMap<String, Double>> patternProbabilityList = new ArrayList<HashMap<String,Double>>();
		for(Map.Entry<String, HashMap<String, Double>> repoEntry:repoProbabilityMap.entrySet()){
			repoList.add(repoEntry.getKey());
			patternProbabilityList.add(repoEntry.getValue());			
		}
		/**
		 * 循环遍历，将每一个
		 */
		for(HashMap<String, Double> ppm:patternProbabilityList){
			int ppmIndex = patternProbabilityList.indexOf(ppm);
			String repo = repoList.get(ppmIndex);
			Double individuationSequenceNum = 0.0;
			for(Map.Entry<String, Double> entry:ppm.entrySet()){
				Double numerator = entry.getValue();
				Double denominator = 0.0;
				String pattern = entry.getKey();
				for(int i = 0;i<patternProbabilityList.size();i++){
					if(i==ppmIndex){
						continue;
					}
					HashMap<String, Double> oppm = patternProbabilityList.get(i);
					if(oppm.containsKey(pattern)){
						denominator +=oppm.get(pattern);
					}
				}
				/**
				 * 得到模式可用性度量
				 * 如果分母为0说明这个模式只出现在该项目中
				 */
				Double x=entry.getValue();
				if(denominator==0.0){
					if(entry.getValue()>0.2)
					individuationSequenceNum++;
				}else {
					Double PUM = numerator/(denominator/patternProbabilityList.size());
					if(PUM>AnalyseConstant.INDIVIDUATION_INDEX){
						individuationSequenceNum++;
					}
				}					
			}
			/**
			 * 把结果添加进Map中
			 */
			repoIndividuationSequenceMap.put(repo, individuationSequenceNum);
			individuationSequenceNum = 0.0;
		}
		for(Map.Entry<String, Double> entry:repoIndividuationSequenceMap.entrySet()){
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		return repoIndividuationSequenceMap;
	}
	
	/**
	 * 输入模式文件，得到在这个支持度下，某个算法下，每一个序列的长度以及同样长度的个数
	 * @param patternFile
	 * @return
	 */
	public static HashMap<Integer, Integer> getSequenceLengthAndCount(String patternFile){
		File file = new File(patternFile);
		if(!file.exists()){
			return null;
		}
		HashMap<Integer, Integer> SLAMap = new HashMap<Integer, Integer>();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String temp = "";
			while((temp=bf.readLine())!=null){
				String pattern = temp.split("\\|->")[0];
				String subPattern[] = pattern.split("\\|");
				Integer len = subPattern.length;
				if(SLAMap.containsKey(len)){
					Integer count = SLAMap.get(len);
					SLAMap.put(len, ++count);
				}else {
					SLAMap.put(len, 1);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!SLAMap.isEmpty()){
			for(Map.Entry<Integer, Integer> entry : SLAMap.entrySet()){
				System.out.println(entry.getKey() +" "+entry.getValue()) ;
			}
		}
		return SLAMap;
		
	}
	
	/**
	 * 获取开发者两次行为的平均时间间隔随开发者参与人数增加的变化
	 * @param patternFile
	 * @return
	 */
	public static HashMap<Integer, Double> getAverageTimebyPeople(String patternFile){
		File file = new File(patternFile);
		if(!file.exists()){
			return null;
		}
		HashMap<Integer, Double> ATMap = new HashMap<Integer, Double>();
		HashMap<Integer, Integer> peopleCountMap = new HashMap<Integer, Integer>();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String temp = "";
			while((temp=bf.readLine())!=null){
				String pattern = temp.trim().split("\\|->")[0];
				String subPattern[] = pattern.split("\\|");
				Integer len = subPattern.length;
				Double totalTime = 0.0;
				Set<String> peopleSet = new HashSet<String>();
				/**
				 * 计算总人数
				 */
				for(String sp:subPattern){
					String name = sp.split("\\+")[0];
					//String name = sp.split(" ")[0];
					peopleSet.add(name);
				}
				Integer peopleNum = peopleSet.size();
				if(peopleNum==1){
					System.out.println();
				}
				if(peopleCountMap.containsKey(peopleNum)){
					Integer count = peopleCountMap.get(peopleNum);
					peopleCountMap.put(peopleNum, ++count);
				}else{
					peopleCountMap.put(peopleNum, 1);
				}
				/**
				 * 计算总的时间数
				 */
				for(String sp:subPattern){
					if(sp.contains("less than 1")){
						totalTime += 1.0;
					}else if (sp.contains("between 1 and 3")) {
						totalTime += 2.0;
					}else if (sp.contains("between 3 and 5")) {
						totalTime += 4.0;
					}else if (sp.contains("between 5 and 7")) {
						totalTime += 6.0;
					}else if (sp.contains("between 1 and 2 week")) {
						totalTime += 10;
					}else if (sp.contains("between 2 and 3 week")) {
						totalTime += 17;
					}else if (sp.contains("between 3 and 4 week")) {
						totalTime += 24;
					}else if (sp.contains("more than 1 month")) {
						totalTime += 30.0;
					}
				}
				totalTime /=(len-1);
				if(ATMap.containsKey(peopleNum)){
					Double time = ATMap.get(peopleNum);
					Double newTime = totalTime+time;
					ATMap.put(peopleNum, newTime);
				}else {
					ATMap.put(peopleNum, totalTime);
				}
				
			}
			
			for(Map.Entry<Integer, Double> entry:ATMap.entrySet()){
				Integer peopleNum = entry.getKey();
				Double averageTime = ATMap.get(peopleNum)/peopleCountMap.get(peopleNum);
				System.out.println(peopleNum+" "+averageTime);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!ATMap.isEmpty()){
//			for(Map.Entry<Integer, Integer> entry : ATMap.entrySet()){
//				System.out.println(entry.getKey() +" "+entry.getValue()) ;
//			}
		}
		return null;
	}
	
	/**
	 *计算抽象模式中，有多少模式是从具体用户模式中抽象而来，有多少模式原来没有在具体用户模式中但是是新加入进来的
	 *
	 * @param abstractPatternFile
	 * @param specificPatternFile
	 * @return
	 */
	public static Double getExistAndNewRatio (String abstractPatternFile,String specificPatternFile){
		/**
		 * 最后要返回的比例值
		 */
		Double ratio = 0.0;
		File specificFile = new File(specificPatternFile);
		if(!specificFile.exists()){
			return null;
		}
		File abstractFile = new File(abstractPatternFile);
		if(!abstractFile.exists()){
			return null;
		}
		/*将具体用户的模式转换成抽象的模式，存放在fromSpecific中
		 * 		  
		 */
		HashSet<String> patternFromSpecific = new HashSet<String>();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(specificFile)));
			String specificPattern = "";
			while((specificPattern=bf.readLine())!=null){
				specificPattern = specificPattern.trim().split("\\|->")[0];
				String behaviors[] = specificPattern.split("\\|");
				/**
				 * 遍历每个行为，并提取出行为的发起者，对行为的发起者的先后顺序进行标号
				 */
				String actor = "";
				Integer actorIndex = 0;
				HashMap<String, String> actorIndexMap = new HashMap<String, String>();
				for(String s:behaviors){
					String elements [] = s.split(" ");
					actor = elements[0];
					if(actor.isEmpty()){
						break;
					}
					if(!actorIndexMap.containsKey(actor)){
						actorIndex++;
						actorIndexMap.put(actor, String.valueOf(actorIndex));
					}
				}
				if (!actor.isEmpty()) {
					for(Map.Entry<String,String> entry:actorIndexMap.entrySet()){
						specificPattern = specificPattern.replaceAll(entry.getKey(), entry.getValue());
					}
					patternFromSpecific.add(specificPattern);
					/**
					 * 将编码完成的pattern存放在set中
					 */
					//System.out.println(specificPattern);
				}
				actorIndexMap.clear();
			}
				//System.out.println(patternFromSpecific.size());
				bf.close();
				/**
				 * 将编码完成的pattern存放在set中
				 */
				BufferedReader bf2 = new BufferedReader(new InputStreamReader(new FileInputStream(abstractFile)));
				String abstractPattern = "";
				Double patternCount=0.0;
				Double old = 0.0;
				double alpha = 0.07;
				while((abstractPattern=bf2.readLine())!=null){
					patternCount++;
					abstractPattern = abstractPattern.split("\\|->")[0];
					abstractPattern = abstractPattern.replaceAll("\\+", " ");
					if(patternFromSpecific.contains(abstractPattern)){
						old++;
					}
				}
				ratio = old/patternCount;
				System.out.println(ratio);
				return ratio;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ratio;
	}
	
	
	@SuppressWarnings("static-access")
	public static void main(String args[]){
		GraphData g = new GraphData();
		//g.shannonWienerGraph("AAGM", "golang-go");
		//g.individuationSequenceGraph();
		//g.getSequenceLengthAndCount("/Users/mac/git/gitdata/gitdata-patnanalysis/ptnresult/AAGM-Microsoft-TypeScript-5%.txt");
		
		//g.getAverageTimebyPeople("/Users/mac/git/gitdata/gitdata-patnanalysis/ptnresult/AATGM-DefinitelyTyped-DefinitelyTyped-9.txt");
		
		
		
		/**
		 * 
		 */
		for(int i=1;i<=5;i++){
			g.getExistAndNewRatio("/Users/mac/git/gitdata/gitdata-patnanalysis/ptnresult/AAGM-golang-go-1%.txt",
					"/Users/mac/git/gitdata/gitdata-patnanalysis/ptnresult/SGM-golang-go-"+i+"%.txt");
		}
		
		
	}
	
}
