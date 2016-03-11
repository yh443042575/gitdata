package edu.hit.yh.gitdata.mine.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

/**
 * 用来将数据库中用户的行为记录导出CSV格式的数据的工具类
 * @author DHAO
 *
 */
public class ExportUtil {

	/**
	 * 导出指定的repository下的 
	 * @param repo
	 * @throws IOException 
	 */
	public static void exportIssueDataAsCsv(String repo) throws IOException{
		List<Artifact<SimpleBehavior>> artifactList = buildArtifacts(repo, "Issue");
		File file = new File("G://IssueDataAsCsv//IssueData_mrdoob_three.js.csv");
		if(!file.exists()){
			file.createNewFile();
		}
		
		
		BufferedWriter bf = new BufferedWriter(new FileWriter(file));
		String string = "actor,eventType,createAt,artifactId";
		bf.write(string);
		bf.write("\r\n");
		for(Artifact<SimpleBehavior> art:artifactList){
			for(SimpleBehavior s:art.getBehaviorSeq()){
				String info=s.getActor()+","+s.getEventType()+","+s.getCreatedAt()+","+art.getArtifactId();
				System.out.println(info);
				bf.write(info);
				bf.write("\r\n");
			}
		}
		bf.close();
		HibernateUtil.closeSessionFactory();
	}
	
	/**
	 * 从数据库中取出指定的repo下的某个类型的artifact，这个list是原始的数据，所有的统计支持度都是从这里下手（已完成-未测试）
	 * artifactType 分为：
	 * 1、issue
	 * 2、pullrequest
	 * 3、commit
	 */
	public static List<Artifact<SimpleBehavior>> buildArtifacts(String repo,
			String ArtifactType) {
        
		List<Artifact<SimpleBehavior>> artifactList = null;
		Class clazz = ArtifactUtil.class;
		Method method = null;
		try {
			method = clazz.getMethod("get" + ArtifactType + "SimpleBehavior",
					String.class);
			artifactList = (List<Artifact<SimpleBehavior>>)method.invoke(clazz.newInstance(), repo);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return artifactList;
	}
	
	
	public static void main(String args[]){
		try {
			ExportUtil.exportIssueDataAsCsv("mrdoob/three.js/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
