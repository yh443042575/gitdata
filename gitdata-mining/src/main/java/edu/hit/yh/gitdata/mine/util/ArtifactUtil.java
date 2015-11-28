package edu.hit.yh.gitdata.mine.util;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

/**
 * 用来从数据库中获取某个repo下面的某个artifact类型的所有行为
 * @author DHAO
 *
 */
public class ArtifactUtil {

	@SuppressWarnings({ "unused", "unchecked" })
	public static List<Artifact<SimpleBehavior>> getissueSimpleBehavior(String repo){
		List<Artifact<SimpleBehavior>> issueArtifacts = new ArrayList<Artifact<SimpleBehavior>>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		//取得跟issuesEvnet有关的行为
		//List<Object[]> issuesEventList = session.createSQLQuery("select actor,target,createdAt,artifactId from issuesEvent where repo='"+repo+"'").list();
		//取得跟issuesEvent有关的评论行为
		List<Object[]> issueCommentEventList = session.createSQLQuery("select actor,target,createdAt,artifactId from issueCommentEvent where repo='"+repo+"' order by artifactId").list();
		
		
		for(Object[] objects : issueCommentEventList ){
			Artifact<SimpleBehavior> artifacts = new Artifact<SimpleBehavior>();
			
			SimpleBehavior simpleBehavior = new SimpleBehavior();
			simpleBehavior.setActor(StringUtil.objectToString(objects[0]));
			simpleBehavior.setTarget(StringUtil.objectToString(objects[1]));
			simpleBehavior.setCreatedAt(StringUtil.objectToString(objects[2]));
			simpleBehavior.setEventType("");
			
			
			System.out.println();
		}
		
		return issueArtifacts;
	}
 	
}
