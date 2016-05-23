package edu.hit.yh.gitdata.analyse.util;

import java.util.Comparator;

/**
 * 为cluster排序
 * @author Dhao
 *
 */
public class ClusterComparator implements Comparator<String>{

	public int compare(String cluster1, String cluster2) {
		String info1[] = cluster1.split("Cluster ");
		String info2[] = cluster2.split("Cluster ");
		
		Integer i1 = Integer.valueOf(info1[1]);
		Integer i2 = Integer.valueOf(info2[1]);
		
		if(i2>i1){
			return -1;
		}
		if(i2.equals(i1)){
			return 0;
		}else {
			return 1;
		}
	}
	
	
}
