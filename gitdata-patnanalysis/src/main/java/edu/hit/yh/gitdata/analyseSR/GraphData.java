package edu.hit.yh.gitdata.analyseSR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.hit.yh.gitdata.grphmodel.ShannonWiener;
import edu.hit.yh.gitdata.mine.constant.DirConstant;

/**
 * 通过统计工具，输出各类图形所需要的数据集
 * @author DHAO
 *
 */
public class GraphData {
	
	/**
	 * 计算每个模式下的丰富度模式丰富度的数据
	 * 通过获取本地文件，解析成图表所需要的文件
	 * @return
	 */
	public List<ShannonWiener> shannonWienerGraph(String algorithm,String repo){	
		
		String filePath = DirConstant.PATTERN_RESULT_FOLDER+algorithm+"-"+repo+"-";
		List<ShannonWiener> swList = new ArrayList<ShannonWiener>();
		for(int i=5;i<=30;i+=5){
			File file = new File(filePath+String.valueOf(i)+".txt");
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
	
	public static void main(String args[]){
		GraphData g = new GraphData();
		g.shannonWienerGraph("SGM", "jquery-jquery");
	}
	
}
