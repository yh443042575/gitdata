package edu.hit.yh.gitdata.analyseSR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.hit.yh.gitdata.mine.constant.DirConstant;

/**
 * 用于产出画各类图形的数据
 * @author DHAO
 *
 */
public class CalculateUtil {

	/**
	 * 初始化repository的list，以供后续遍历使用
	 */
	public static List<String> REPO_LIST = new ArrayList<String>(Arrays.asList(
			"atom-electron",
			"DefinitelyTyped-DefinitelyTyped",
			"docker-docker",
			"FreeCodeCamp-FreeCodeCamp",
			"gogits-gogs",
			"golang-go",
			"jquery-jquery",
			"mrdoob-three.js",
			"Microsoft-TypeScript",
			"zurb-foundation-sites"));
	/**
	 * 初始化repository的list，以供后续遍历使用
	 */
	public static List<String> REPO_LIST_SIMPLE= new ArrayList<String>(Arrays.asList(
			"ETR",
			"DT",
			"DK",
			"FCC",
			"GOG",
			"GO",
			"JQ",
			"TJ",
			"TS",
			"FS"));
	
	
	
	public static HashMap<String, Integer> artifactListSizeMap = new HashMap<String, Integer>();
	static{
		artifactListSizeMap.put("jquery-jquery", 534);
		artifactListSizeMap.put("golang-go", 3219);
		artifactListSizeMap.put("gogits-gogs", 1979);
		artifactListSizeMap.put("FreeCodeCamp-FreeCodeCamp", 1924);
		artifactListSizeMap.put("mrdoob-three.js", 4674);
		artifactListSizeMap.put("DefinitelyTyped-DefinitelyTyped", 1173);
		artifactListSizeMap.put("atom-electron", 3129);
		artifactListSizeMap.put("zurb-foundation-sites", 4341);
		artifactListSizeMap.put("Microsoft-TypeScript", 4667);
		artifactListSizeMap.put("docker-docker", 2158);
	}
	
	
	public static HashMap<String, String> artifactListBuildTimeMap = new HashMap<String, String>();
	static{
		artifactListBuildTimeMap.put("jquery jquery ", "2014-09-13T00:00:00Z");
		artifactListBuildTimeMap.put("atom electron ", "2013-04-07T00:00:00Z");
		artifactListBuildTimeMap.put("golang go ", "2009-10-22T00:00:00Z");
		artifactListBuildTimeMap.put("FreeCodeCamp FreeCodeCamp ", "2013-11-10T00:00:00Z");
		artifactListBuildTimeMap.put("mrdoob three.js ", "2010-03-21T00:00:00Z");
		artifactListBuildTimeMap.put("DefinitelyTyped DefinitelyTyped ", "2012-09-30T00:00:00Z");
		artifactListBuildTimeMap.put("zurb foundation sites ", "2011-10-09T00:00:00Z");
		artifactListBuildTimeMap.put("Microsoft TypeScript ", "2014-07-06T00:00:00Z");
		artifactListBuildTimeMap.put("docker docker ", "2013-01-13T00:00:00Z");
		artifactListBuildTimeMap.put("gogits gogs ", "2014-01-09T00:00:00Z");
	}
	
	/**
	 * 计算香浓指数，用于描述一个项目里，所有的序列模式的信息含量，也就是序列模式丰富度
	 * 输入，每个模式的支持度，所有模式出现的总数
	 */
	public static Double getShannonWiener(List<String> pattern,Double total){
		Double H = 0.0;
		for(String ptn:pattern){
			String[] info = ptn.split("->");
			Double ni = Double.valueOf(info[1]);
			H+=-ni*(Math.log(ni)-Math.log(total))/total;
		}
		return H;
	}
	
	/**
	 * 计算个性化序列个数
	 * 因为要在项目之间横向比较，所以只能分析抽象用户，具体用户的模式序列在其他项目中并不能体现出来
	 * 输入N个项目支持度为10的结果，输出N个Map，每一个Map里包含每一个模式在自身这个项目中出现的概率
	 */
	public static HashMap<String,HashMap<String,Double>> getIndividuationSequence(){
		
		
		//用来存储每个sequence出现可能性的Map
		HashMap<String, HashMap<String,Double>> repoProbabilityMap = new HashMap<String, HashMap<String,Double>>();
		
		//List<String> algorithmList = new ArrayList<String>(Arrays.asList("AAGM","AATGM")) ;
		List<String> algorithmList = new ArrayList<String>(Arrays.asList("AATGM")) ;
		for(String algrithm : algorithmList){
			for(String repo:CalculateUtil.REPO_LIST){
				HashMap<String, Double> sequenceProbabilityMap = new HashMap<String, Double>();
				repoProbabilityMap.put(repo, sequenceProbabilityMap);
				File file = new File(DirConstant.PATTERN_RESULT_FOLDER+algrithm+"-"+repo+"-5%.txt");
				if(!file.exists()){
					continue;
				}
				try {
					//Double totalNum = calculateSequencetotalNum(file);
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					String tempString = "";
					while((tempString=br.readLine())!=null){
						String pattern = tempString.split("->")[0];
						Double d = Double.valueOf(tempString.split("->")[1]);
						Double probability = d/artifactListSizeMap.get(repo);
						sequenceProbabilityMap.put(pattern, probability);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return repoProbabilityMap;
	}
	
	/**
	 * 用于计算序列模式出现的频度总和
	 * @param file
	 * @return
	 */
	public static Double calculateSequencetotalNum(File file){
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			/*
			 * 遍历file，把每一个序列的频度相加
			 */
			String tempString = "";
			Double totalNum = 0.0;
			while((tempString = br.readLine())!=null){
				Double d = Double.valueOf(tempString.split("->")[1]);
				totalNum +=d;
			}
			return totalNum;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 利用1%的最小支持度下生成的模式，推算出2%~5%的模式
	 * @param algorithm
	 * @throws IOException 
	 */
	public static void getOtherSurpportPattern(String algorithm) throws IOException
	{//我想在这里写注释
		for(Map.Entry<String, Integer> entry:artifactListSizeMap.entrySet()){
			String repoName = entry.getKey();
			Integer surpport = entry.getValue();
			File persent1File = new File(DirConstant.PATTERN_RESULT_FOLDER+algorithm+"-"+repoName+"-1%.txt");
			if(!persent1File.exists()){
				continue;
			}
			for(int i=2;i<=5;i++){
				File file = new File(DirConstant.PATTERN_RESULT_FOLDER+algorithm+"-"+repoName+"-"+String.valueOf(i)+"%.txt");
				if(!file.exists()){
					file.createNewFile();
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(persent1File)));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				Integer minSurpport = surpport*i/100;
				String temp = "";
				while((temp=br.readLine())!=null){
					 String s[] = temp.split("\\|->");
					 int patternSurpport = Integer.valueOf(s[1]);
					 if(patternSurpport>minSurpport){
						 bw.write(temp);
						 bw.write("\n");
					 }
				}
				bw.flush();
			}
		}
	}
	
	public static void main(String args[]) throws IOException{
		getOtherSurpportPattern("SGM");
	}
	
	
}
