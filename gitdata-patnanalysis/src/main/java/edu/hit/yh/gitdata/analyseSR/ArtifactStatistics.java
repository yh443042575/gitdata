package edu.hit.yh.gitdata.analyseSR;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.spi.DirStateFactory;

import org.hibernate.loader.custom.Return;
import org.htmlparser.tags.Div;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.dial.DialPointer.Pin;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import com.lowagie.text.pdf.DefaultFontMapper;

import kmeans.kmeans;
import kmeans.kmeans_data;
import kmeans.kmeans_param;
import lombok.val;
import edu.hit.yh.gitdata.analyse.constant.AnalyseConstant;
import edu.hit.yh.gitdata.analyse.util.ClusterComparator;
import edu.hit.yh.gitdata.analyse.util.ClusterUtil;
import edu.hit.yh.gitdata.analyse.util.ExportSvgUtil;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.mine.algorithm.SimpleGspMiningAlgorithm;
import edu.hit.yh.gitdata.mine.constant.DirConstant;
import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;
import edu.hit.yh.gitdata.mine.util.RelativeTimeUtil;

/**
 * 用于统计artifact中的数量特征，进而能够做成一个向量，写入文件中
 * 
 * @author Dhao
 *
 */
public class ArtifactStatistics {

	/**
	 * 对所给的指定repository下的数据进行聚类
	 * @param repo
	 * @return
	 */
	public List<double[][]> getClusters(String repo,int clusterNum) {
		
		double[][] points = buildRawPointSetFromFile(repo);
		kmeans_data data = new kmeans_data(points, points.length, points[0].length); // 初始化数据结构
		kmeans_param param = new kmeans_param(); // 初始化参数结构
		param.initCenterMehtod = kmeans_param.CENTER_RANDOM; // 设置聚类中心点的初始化模式为随机模式
		
		/*
		 * 做kmeans计算，分两类
		 */
		kmeans.doKmeans(clusterNum, data, param);
		
		List<List<double[]>> clusterList = new ArrayList<List<double[]>>(clusterNum);
		for(int i=0;i<clusterNum;i++){
			clusterList.add(new ArrayList<double[]>());
		}
		
		for(int i=0;i<points.length;i++){
			int lable = data.labels[i];
			double[] point = points[i];
			clusterList.get(lable).add(point);
		}
		
		/*
		 * 得到最后的二维矩阵
		 */
		List<double[][]> clusters = new ArrayList<double[][]>();

		for(List<double[]> labelPoints:clusterList){
			double[][] pointMatrix = new double[labelPoints.size()][];
			for(int i=0;i<labelPoints.size();i++){
				pointMatrix[i] = labelPoints.get(i);
			}
			clusters.add(pointMatrix);
		}
		
		return clusters;
	}
	
	public kmeans_data getOutOfOrderClusters(String repo ,int clusterNum){
		double[][] points = buildRawPointSetFromFile(repo);
		kmeans_data data = new kmeans_data(points, points.length, points[0].length); // 初始化数据结构
		kmeans_param param = new kmeans_param(); // 初始化参数结构
		param.initCenterMehtod = kmeans_param.CENTER_RANDOM; // 设置聚类中心点的初始化模式为随机模式
		
		/*
		 * 做kmeans计算，分两类
		 */
		kmeans.doKmeans(clusterNum, data, param);
		return data;
	}
	
	
	/**
	 * 得到所有repository的原始数据，保存到本地路径
	 */
	public void getAllRepoRawData() {
		for (String repo : CalculateUtil.REPO_LIST) {
			try {
				repo = repo.replaceFirst("-", "/");
				writeDataFeatureToFile(repo + "/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HibernateUtil.closeSessionFactory();
	}

	
	/**
	 * 将指定repository下的所有artifact整理出来
	 * 
	 * @param repo
	 * @param artifactType
	 * @return
	 */
	private List<Artifact<SimpleBehavior>> getArtifacts(String repo,
			String artifactType) {
		SimpleGspMiningAlgorithm sgm = new SimpleGspMiningAlgorithm(0);
		return sgm.buildArtifacts(repo, artifactType);
	}

	/**
	 * 得到某个repo下的所有数据特征，整理成向量，写入本地，用作聚类分析用
	 * 
	 * 输出文档，从左到右的属性依次是：距当前时间下(假设为4月1日0点),协作的总时间，协作的总个数，行为时间间隔分布,参与开发者的人数
	 * 
	 * 其中行为时间间隔分布取三个值，最大值最小值和中间值
	 * 
	 * @param artifacts
	 * @throws IOException
	 */
	public void writeDataFeatureToFile(String repo) throws IOException {

		System.out.println(repo);
		List<Artifact<SimpleBehavior>> artifacts = this.getArtifacts(repo,
				"Issue");
		repo = repo.replaceAll("/", "_");
		repo = repo.replaceAll("-", "_");
		File file = new File(DirConstant.ARTIFACT_RESULT_FOLDER
				+ "artifactRawData_" + repo + ".txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file)));
		bw.write(/*距今时间*/"协作总时间,协作总步数,间隔时间最大值,间隔时间最小值,间隔时间中位数,参与开发者人数,评论数\r\n");

		for (Artifact<SimpleBehavior> artifact : artifacts) {

			List<SimpleBehavior> behaviors = artifact.getBehaviorSeq();
			/*
			 * 如果协作的总步数小于等于1，就视为没有协作
			 */
			if(behaviors.size()<=1){
				continue;
			}
			
			
			// bug距今有多长时间
			SimpleBehavior firstBehavior = artifact.getBehaviorSeq().get(0);
			/*Double daysAgo = RelativeTimeUtil.getTimeUnitNum(
					CalculateUtil.artifactListBuildTimeMap.get(repo.replaceAll("_", " ")),firstBehavior.getCreatedAt(), 30*86400000L);
*/
			// 协作的总个数
			Integer behaviorNum = artifact.getBehaviorSeq().size();

			// bug协作的总时间
			SimpleBehavior lastBehavior = artifact.getBehaviorSeq().get(
					behaviorNum - 1);
			Double totalTime = RelativeTimeUtil.getTimeUnitNum(
					firstBehavior.getCreatedAt(), lastBehavior.getCreatedAt(),86400000L);

			// 参与artifact的总人数
			Set<String> peopleSet = new HashSet<String>();
			for (String person : artifact.getActors()) {
				peopleSet.add(person);
			}
			Integer peopleNum = peopleSet.size();

			// 获取行为时间间隔分布,去时间分布的最大值最小值与中间值
			List<Double> relativeTimeList = new ArrayList<Double>();
			for (int i = 0; i < behaviorNum - 1; i++) {
				String time1 = behaviors.get(i).getCreatedAt();
				String time2 = behaviors.get(i + 1).getCreatedAt();
				Double days = RelativeTimeUtil.getTimeUnitNum(time1, time2,86400000L);
				relativeTimeList.add(days);
			}
			if (behaviorNum == 1) {
				relativeTimeList.add(-1.0);
			}
			Collections.sort(relativeTimeList);
			Double minRelativeTime = relativeTimeList.get(0)*24;
			Double maxRelativeTime = relativeTimeList
					.get(behaviorNum > 1 ? behaviorNum - 2 : 0);
			Double middleRelativeTime = relativeTimeList
					.get((behaviorNum > 1 ? behaviorNum - 2 : 0) / 2)*24;

			//获取整个artifact中的评论的数量
			int issueCommentNum = 0;
			for(int i=0;i<behaviors.size();i++){
				if (behaviors.get(i).getEventType().equals("issueComment")) {
					issueCommentNum++;
				}
			}
			
			//bw.write(String.valueOf(daysAgo) + ",");
			bw.write(String.valueOf(totalTime) + ",");
			bw.write(String.valueOf(behaviorNum) + ",");
			bw.write(String.valueOf(maxRelativeTime) + ",");
			bw.write(String.valueOf(minRelativeTime) + ",");
			bw.write(String.valueOf(middleRelativeTime) + ",");
			bw.write(String.valueOf(peopleNum) + ",");
			bw.write(String.valueOf(issueCommentNum) + "\r\n");
			bw.flush();

		}
		bw.close();
	}

	/**
	 * 计算聚类的Xie-Beni指数：用于评价聚类的好坏，公式为 分子：∑每个点到其聚类内部的距离的平方，在对每个聚出来的类做同样的操作
	 * 分母：类与类之间最小的距离的平方*聚类的个数n
	 * 
	 * 最小值即为最好的分类方式
	 * 
	 * @return
	 */
	public static double getXieBeniIndex(List<double[][]> clusters) {
		double numerator = 0.0;
		double denominator = 0.0;
		List<double[]> centroids = new ArrayList<double[]>();
		for (double[][] pointSet : clusters) {
			// 得到质心
			if(pointSet.length==0){
				System.out.println("errer01");
				
			}
			double[] centroid = ClusterUtil.getCentroid(pointSet);
			centroids.add(centroid);
			// 距离平方和
			for (double[] point : pointSet) {
				numerator += ClusterUtil.getEuclidDistanceQuadratic(point,
						centroid);
			}
		}

		List<Double> distanceList = new ArrayList<Double>();
		for (int i = 0; i < centroids.size(); i++) {
			for (int j = i + 1; j < centroids.size(); j++) {
				double[] point1 = centroids.get(i);
				double[] point2 = centroids.get(j);
				Double d= ClusterUtil.getEuclidDistanceQuadratic(point1,point2);
				distanceList.add(d);
			}
		}
		Collections.sort(distanceList);
		denominator = distanceList.get(0);
		denominator *= clusters.size();

		return numerator / denominator;
	}

	/**
	 * 将指定repository的文件转换成二维double数组
	 * @param repo
	 * @return
	 */
	private double[][] buildRawPointSetFromFile(String repo) {
		
		try {
			File file = new File(DirConstant.ARTIFACT_RESULT_TO_1_FOLDER
					+ "artifactRawData_" + repo.replace('-', '_') + "_to1.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String data = br.readLine();
			List<double[]> points = new ArrayList<double[]>();
			while ((data = br.readLine()) != null) {
				String [] s = data.split(",");
				double[] point = new double[s.length];
				for(int i=0;i<s.length;i++){
					point[i] = Double.valueOf(s[i]);
				}
				points.add(point);
			}
			double[][] pointSet = new double[points.size()][];
			for(int i=0;i<points.size();i++){
				pointSet[i] = points.get(i);
			}
			return pointSet;
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
	 * 对每一个已知的类进行分析，分类个数从2-9，文件命名格式为类名-分类个数-xiebeni指数，
	 * 每个repository的分析结果都在其各自的名字的文件夹下
	 * 
	 * 每一个分析结果中，比原来的电多了一列，即是类标号
	 * @throws IOException 
	 * 
	 */
	public void persistentAnalyseResult() throws IOException{
		for(String repo:CalculateUtil.REPO_LIST){
			String path = DirConstant.ARTIFACT_ANALYSE_RESULT_FOLDER+repo;
			File filePath = new File(path);
			if(!filePath.exists()){
				filePath.mkdirs();
			}       
			for(int i=2;i<15;i++){
				List<double[][]> clusters = getClusters(repo, i);
				boolean flag = true;
				for(double[][] d:clusters){
					if(d.length == 0){
						flag = false;
					}
				}
				if(!flag){
					break;
				}
				double xiebeni = getXieBeniIndex(clusters);
				DecimalFormat decimalFormat = new DecimalFormat("###.000");
				File clusterFile = new File(filePath
						+"/"+repo
						+"-"+i
						+"-"+decimalFormat.format(xiebeni)
						+".txt");
				if(!clusterFile.exists()){
					clusterFile.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(clusterFile)));
				for(String head:AnalyseConstant.ANALYSE_RESULT_HEADER){
					bw.write(head+",");
				}
				bw.write("\r\n");
				for(int j=0;j<clusters.size();j++){
					double[][] cluster = clusters.get(j);
					for(double[] point:cluster){
						bw.write(j+",");
						for(double featrue:point){
							bw.write(featrue+",");
						}
						bw.write("\r\n");
					}
					bw.flush();
				}
			}
		}
	}
	
	/**
	 * 函数重载，专门为所有的
	 * @param repo
	 * @throws IOException 
	 */
	public void persistentAnalyseResult(String repo) throws IOException{
			
			String path = DirConstant.ARTIFACT_ANALYSE_RESULT_FOLDER+repo;
			File filePath = new File(path);
			if(!filePath.exists()){
				filePath.mkdirs();
			}       
			for(int i=2;i<15;i++){
				List<double[][]> clusters = getClusters(repo, i);
				boolean flag = true;
				for(double[][] d:clusters){
					if(d.length == 0){
						flag = false;
					}
				}
				if(!flag){
					break;
				}
				double xiebeni = getXieBeniIndex(clusters);
				DecimalFormat decimalFormat = new DecimalFormat("###.000");
				File clusterFile = new File(filePath
						+"/"+repo
						+"-"+i
						+"-"+decimalFormat.format(xiebeni)
						+".txt");
				if(!clusterFile.exists()){
					clusterFile.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(clusterFile)));
				for(String head:AnalyseConstant.ANALYSE_RESULT_HEADER){
					bw.write(head+",");
				}
				bw.write("\r\n");
				for(int j=0;j<clusters.size();j++){
					double[][] cluster = clusters.get(j);
					for(double[] point:cluster){
						bw.write(j+",");
						for(double featrue:point){
							bw.write(featrue+",");
						}
						bw.write("\r\n");
					}
					bw.flush();
				}
			}
		}

	
	
	public void getClusterFeature() throws IOException{
		File filePathFile = new File(DirConstant.ARTIFACT_MIN_VALUE_RESULT);
		String files[] =  filePathFile.list();
 		File resultFile = new File(DirConstant.ARTIFACT_MIN_VALUE_RESULT+"resultFile.txt");
 		if(!resultFile.exists()){
 			resultFile.createNewFile();
 		}
 		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile)));
		/*
 		 * 遍历每一个文件，求得每一个类的最大值最小值与平均值
 		 */
		for(String f:files){
			if(f.equals("resultFile.txt")){
				continue;
			}
			File file = new File(DirConstant.ARTIFACT_MIN_VALUE_RESULT+f);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
 			String point = br.readLine();
 			String values[] = point.split(",");
 			int clusterNum = 0;
 			List<double[]> maxMinList = new ArrayList<double[]>();
 			double[] max = new double[values.length];
			double[] min = new double[values.length];
			double[] middle = new double[values.length];
			for(int i=0;i<values.length;i++){
					min[i] = Double.MAX_VALUE; 
			}
			
			int pointNum = 0;
 			System.out.println(f);
			while((point = br.readLine())!=null){
 				values = point.split(",");
 				
 				if(clusterNum!=Integer.valueOf(values[0])){//如果类标号变了，即变为了下一个类了
 					clusterNum = Integer.valueOf(values[0]);
 					maxMinList.add(min);
 					for(int i=0;i<middle.length;i++){
 						middle[i] /= pointNum;
 					}
 					maxMinList.add(middle);
 					maxMinList.add(max);
 					pointNum = 0;
 					max = new double[values.length];
 					min = new double[values.length];
 					middle = new double[values.length];
 					
 					for(int i=0;i<values.length;i++){
 						min[i] = Double.MAX_VALUE; 
 					}
 				}
 				pointNum++;
 				for(int i=0;i<values.length;i++){
 					if(Double.valueOf(values[i])>max[i]){
 						max[i] = Double.valueOf(values[i]);
 					}
 					if(Double.valueOf(values[i])<min[i]){
 						min[i] = Double.valueOf(values[i]);
 					}
 					middle[i]+=Double.valueOf(values[i]);
 				}
 			}
				maxMinList.add(min);
			for(int i=0;i<middle.length;i++){
					middle[i] /= pointNum;
				}
				maxMinList.add(middle);
				maxMinList.add(max);
				
			/**
			 * 当一个file解析完毕，将所有解析的结果写到结果分析文件中
			 */
			DecimalFormat decimalFormat = new DecimalFormat("####0.000");
			bw.write(f+"\r\n");
			for(double[] features:maxMinList){
				for(double value:features){
					bw.write(decimalFormat.format(value)+",");
				}
				bw.write("\r\n");
			}
			bw.flush();
			
 		}
	}
	
	/**
	 * 对上面处理的数据进行归一化
	 * @throws IOException 
	 */
	public void makeDataTo1() throws IOException{
		File pathFile = new File(DirConstant.ARTIFACT_RESULT_FOLDER);
		String fileNames[] = pathFile.list();
		for(String fileName:fileNames){
			
			File file = new File(DirConstant.ARTIFACT_RESULT_FOLDER+fileName);
			BufferedReader br =  new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			List<double[]> pointList = new ArrayList<double[]>();
			String temp = "";
			String header =  br.readLine();
			int divNum = header.split(",").length;
			//存储最大值与最小值的向量，用于归一化用
			double[] max = new double[divNum];
			double[] min = new double[divNum];
			for(int i=0;i<divNum;i++){
				max[i] = Double.MIN_VALUE;
				min[i] = Double.MAX_VALUE;
			}
			/**
			 * 循环遍历文件，同时将这个项目中的所有点集拿出来计算出其中的最大最小值，用于归一化使用
			 */
			while((temp = br.readLine())!=null){
				String features[] = temp.split(",");
				double[] point = new double[features.length];
				pointList.add(point);
				for(int j=0;j<features.length;j++){
					String fe = features[j];
					//加上一个小数点能够强制转换成double类型的数据
					if(!fe.contains(".")){
						fe+=".0";
					}
					point[j] = Double.valueOf(fe);
					if(point[j]>max[j]){
						max[j] = point[j];
					}
					if(point[j]<min[j]){
						min[j] = point[j];
					}
				}
			}
			/**
			 * 得到每个列的最大值最小值之后，计算归一化数值，并存储到另外的文件夹中
			 */
			File to1FilePath = new File(DirConstant.ARTIFACT_RESULT_TO_1_FOLDER);
			if(to1FilePath.exists()){
				to1FilePath.mkdirs();
			}
			File to1File = new File(DirConstant.ARTIFACT_RESULT_TO_1_FOLDER+fileName.replace(".txt", "to1.txt"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to1File)));
			//输出表头
			bw.write(header);
			//循环输出新的归一化后的点
			for(double[] oldPoint:pointList){
				bw.write("\r\n");
				double[] newPoint = new double[divNum];
				for(int i=0;i<divNum;i++){
					newPoint[i] = (oldPoint[i]-min[i])/(max[i]-min[i]);
					bw.write(String.valueOf(newPoint[i]));
					if(i!=divNum-1){
						bw.write(",");
					}
				}
			}
			br.close();
			bw.close();
		}
	}
	
	/**
	 * 通过分析得到的各个repository下的各个类的质心，做成spider图，构造出spider的数据
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	private void buildSpiderGraph () throws IOException{
		
		List<String> dimName = AnalyseConstant.SPIDER_HEADER_IN_ENGLISH;
		File file = new File(DirConstant.ARTIFACT_MIN_VALUE_RESULT+"resultFile.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		JFreeChart chart = null;
		String temp = "";
		List<String> tempdData = new ArrayList<String>();
		Map<String,  Map<String, Object>> graph = new TreeMap<String, Map<String, Object>>();				
		DefaultCategoryDataset categoryDataset =  MySpiderWebPlot.createDataSet2(graph,dimName);
		MySpiderWebPlot mySpiderWebPlot = new MySpiderWebPlot(categoryDataset);
		mySpiderWebPlot.setMaxValue(1.0);
		String pngName = "";
		while((temp=br.readLine())!=null){
			if(temp.contains(".txt")){
				//如果chart不等于null则将当前chart中的数据转换成图输出——————（未完成）
				if(!graph.isEmpty()){
					File png = new File(DirConstant.SPIDER_PNG+pngName);
					if(!png.exists()){
						png.createNewFile();
					}
					categoryDataset =  MySpiderWebPlot.createDataSet2(graph,dimName);
					mySpiderWebPlot = new MySpiderWebPlot(categoryDataset);
					mySpiderWebPlot.setMaxValue(1.0);
					mySpiderWebPlot.setLabelFont(new Font("SansSerif", Font.BOLD, 20));
					chart = new JFreeChart(mySpiderWebPlot);
					chart.setBackgroundPaint(Color.WHITE);
					chart.setBackgroundImageAlpha(1.0f);
					chart.setBorderVisible(false);
					//将我们生成的图导出成图片
					ChartUtilities.saveChartAsJPEG(png, chart, 500, 500);
					//CreatePDF.saveChartAsPDF(file, chart, 600, 500, new DefaultFontMapper());
					ExportSvgUtil.saveChartAsSvg(chart, 500, 500, png);
					ChartFrame chartFrame=new ChartFrame(pngName,chart); 
					chartFrame.setVisible(false);
				}
				graph = new TreeMap<String, Map<String, Object>>(new ClusterComparator());		
				
				pngName = temp.replace(".txt", ".svg");
				
			}else {
				DecimalFormat decimalFormat = new DecimalFormat("0.##");
				String centroid[] = br.readLine().split(","); 
				Map<String, Object> subGraph = new HashMap<String, Object>();
				graph.put("Cluster "+decimalFormat.format(Double.valueOf(centroid[0])), subGraph);
				for(int i=0;i<dimName.size();i++){
					subGraph.put(dimName.get(i), Double.valueOf(centroid[i+1]));
				}
				br.readLine();
			}
			
		}
		
	}
	
	/**
	 * 对10个项目的所有属性做boxplot的处理，一共7个属性，则生成7张图，每一个图有10个box，一个box对应一个项目
	 * 
	 * 从归一化的数据中组织成一个三维的数组
	 * @throws IOExcept 
	 * 
	 */
	private void buildBoxPlot() throws IOException{
		
		List<List<double[]>> boxPlotDataList = new ArrayList<List<double[]>>();
		File filePath = new File(DirConstant.ARTIFACT_RESULT_FOLDER);
		String filesName [] = filePath.list();
		
		/**
		 * 有几个属性，就输出几张图,每个list中包含10个项目的每一列数据
		 */
		for(int i=0;i<AnalyseConstant.PROPERTY_NUM;i++){
			List<double[]> boxPlotData = new ArrayList<double[]>();
			boxPlotDataList.add(boxPlotData);
		}
		
		/**
		 * 遍历每一个文件，把8个属性对应的数据全都制作成数组
		 */
		for(int i=0;i<filesName.length;i++){
			File artifactDataFile = new File(DirConstant.ARTIFACT_RESULT_FOLDER+filesName[i]);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(artifactDataFile)));
			br.readLine();
			String temp = "";
			List<List<Double>> featruesValueList = new ArrayList<List<Double>>();
			for(int j=0;j<AnalyseConstant.PROPERTY_NUM;j++){
				List<Double> columnValue = new ArrayList<Double>(); 
				featruesValueList.add(columnValue);
			}
			
			while((temp = br.readLine())!=null){
				String features[] = temp.split(",");
				for(int j=0;j<features.length;j++){
					Double tempDouble = Double.valueOf(features[j]);
					//调整时间间隔的取值
					if(j==1) {//协作总时间
						if(tempDouble>1000){
							continue;
						}
					}
					if(j==2) {//最大值
						if(tempDouble>300){
							continue;
						}
					}else if(j==3) {//最小值
						if(tempDouble<0.025){
							continue;
						}if(tempDouble>48){
							continue;
						}
					}else if (j==4) {//中间值
						if(tempDouble>72){
							continue;
						}
					}
					featruesValueList.get(j).add(tempDouble);
					
				}
			}
			
			for(int j=0;j<AnalyseConstant.PROPERTY_NUM;j++){
				List<Double> columnValue = featruesValueList.get(j);
				double[] data= new double[columnValue.size()];
				for(int k=0;k<columnValue.size();k++){
					data[k] = columnValue.get(k);
				}
				boxPlotDataList.get(j).add(data);
			}
		}
		
		for(List<double[]> graphData :boxPlotDataList){
			final MyBoxPlot demo = new MyBoxPlot(AnalyseConstant.SPIDER_HEADER_IN_ENGLISH.get(boxPlotDataList.indexOf(graphData)),graphData
					,DirConstant.BOX_PLOT_PNG);  
	        demo.pack();  
	        RefineryUtilities.centerFrameOnScreen(demo);  
	        demo.setVisible(true);  
		}
		
		//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
		
		
		
		
	}
	
	/**
	 * 对所有的项目进行分类，并挑选出最佳分类方式，并画出蛛网图，同时计算每个项目中的数据都落在了哪些类中
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	private double[][] buildAllRepositoriesClusterTable() throws IOException {
		//结果所需要返回的统计结果
		double[][] calculateResult = new double[4][CalculateUtil.REPO_LIST.size()];
		double[][] allRawData = buildRawPointSetFromFile("all");
		kmeans_data data =  getOutOfOrderClusters("all",4);
		//遍历所有的labels得到每一个点专属于哪一个类，最后将所有的结果汇总到一个二维表中
		int point = 0;
		int flag = 0;
		for(int i=0;i<CalculateUtil.REPO_LIST.size();i++){
			String repo = CalculateUtil.REPO_LIST.get(i);
			flag = CalculateUtil.artifactListSizeMap.get(repo);
			int tempEnd =  point+flag-1;
			for(;point<tempEnd;point++){
				int label = data.labels[point];
				calculateResult[label][i]++;
			}
		}
		
		for(int i=0;i<4;i++){
			double[] cluster = calculateResult[i];
			for(int j=0;j<CalculateUtil.REPO_LIST.size();j++){
				String repo = CalculateUtil.REPO_LIST.get(j);
				//对每个类中每个项目下的点个数，都除以项目的总得点个数，得到百分比值
				cluster[j]/=CalculateUtil.artifactListSizeMap.get(repo);
			}
 		}
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		
		for(String repo:CalculateUtil.REPO_LIST){
			System.out.print(repo+" ");
		}
		System.out.println();
		for(int i=0;i<calculateResult.length;i++){
			double[] clusters = calculateResult[i];
			System.out.print("C"+i+" ");
			for(double value:clusters){
				System.out.print(decimalFormat.format(value)+" ");
			}
			System.out.println();
		}
		
		//结果输出路径
		String path = DirConstant.ARTIFACT_ANALYSE_RESULT_FOLDER+"all";
		File filePath = new File(path);
		if(!filePath.exists()){
			filePath.mkdirs();
		}
		return calculateResult;
	}
	
	
	
	public static void main(String args[]) throws IOException {
		/*
		 * 对数据库中的数据进行加工，并将整理得到的数据持久化到本地
		 */
		ArtifactStatistics artifactStatistics = new ArtifactStatistics();
		//artifactStatistics.getAllRepoRawData();
		//artifactStatistics.makeDataTo1();
		/*
		 * 对持久化到本地的数据再进行加工，适应kmeans的输出
		 */
		/*try {
			artifactStatistics.persistentAnalyseResult();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*List<double[][]> clusters = artifactStatistics.getClusters("jquery-jquery", 10);
		System.out.println(getXieBeniIndex(clusters));*/
		//artifactStatistics.getClusterFeature();
		//对每一个项目的每一个熟悉，画出spider图
		artifactStatistics.buildSpiderGraph();
		//对每个属性，对所有的项目进行统计，画出盒图
		//artifactStatistics.buildBoxPlot();
		//artifactStatistics.buildAllRepositoriesClusterTable();
			
		
	}
	
}
