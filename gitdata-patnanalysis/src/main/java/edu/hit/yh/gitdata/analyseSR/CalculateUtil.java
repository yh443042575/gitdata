package edu.hit.yh.gitdata.analyseSR;

import java.util.List;

/**
 * 用于产出画各类图形的数据
 * @author DHAO
 *
 */
public class CalculateUtil {

	/**
	 * 计算香浓指数，用于描述一个项目里，所有的序列模式的信息含量，也就是序列模式丰富度
	 * 输入，每个模式的支持度，所有模式出现的总数
	 */
	public Double getShannonWiener(List<String> pattern,Double total){
		Double H = 0.0;
		for(String ptn:pattern){
			String[] info = ptn.split("->");
			Double ni = Double.valueOf(info[1]);
			H+=ni/(total*Math.log(ni)/Math.log(total));
		}
		return H;
	}
	
	
	
}
