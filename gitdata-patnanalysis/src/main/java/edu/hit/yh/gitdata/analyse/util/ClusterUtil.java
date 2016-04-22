package edu.hit.yh.gitdata.analyse.util;

/**
 * 用于完成聚类相关操作的类
 * @author Dhao
 *
 */
public class ClusterUtil {

	/**
	 * 从所给的点集生成质心
	 * @param pointSet
	 * @return
	 */
	public static double[] getCentroid(double[][] pointSet){
		
		double[] centroid = new double[pointSet[0].length];
		
		/**
		 * 对点集中的所有点进行求和相加
		 */
		for(double[] point:pointSet){
			for(int i=0;i<point.length;i++){
				centroid[i]+=point[i];
			}
		}
		for(int i=0;i<centroid.length;i++){
			centroid[i]/=pointSet.length;
		}
		
		return centroid;
		
	}
	
	/**
	 * 得到两个点的欧几里得距离的平方
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double getEuclidDistanceQuadratic(double[] p1,double[] p2){
		double result = 0.0;
		for(int i=0;i<p1.length;i++){
			 result+=(p1[i]-p2[i])*(p1[i]-p2[i]);
		 }
		return result;
	}
	
	
}
