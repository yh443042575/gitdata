package edu.hit.yh.gitdata.mine.util;

import java.util.List;

import edu.hit.yh.gitdata.mine.module.BehaviorPattern;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

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
	 */
	public void exportSimpleBehaviorGraph(List<BehaviorPattern<SimpleBehavior>> behaviorPatternList,String dir){
		//最终生成的图形dot代码存在sb中
		StringBuilder sb=new StringBuilder();
		for(BehaviorPattern<SimpleBehavior> pattern :behaviorPatternList){
			
		}
		
	}
	
	
	
	
}
