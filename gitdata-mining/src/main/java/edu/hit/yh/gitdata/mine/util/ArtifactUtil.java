package edu.hit.yh.gitdata.mine.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;
import edu.hit.yh.gitdata.mine.module.SimpleBehaviorComparator;

/**
 * 用来从数据库中获取某个repo下面的某个artifact类型的所有行为,准备挖掘使用
 * @author DHAO
 *
 */
public class ArtifactUtil {
	
	/**
	 * 获取跟Issue有关的行为
	 * @param repo
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public static List<Artifact<SimpleBehavior>> getIssueSimpleBehavior(String repo){
		List<Artifact<SimpleBehavior>> issueArtifacts = new ArrayList<Artifact<SimpleBehavior>>();
		Map<String, Artifact<SimpleBehavior>> issueArtifactsMap = new HashMap<String, Artifact<SimpleBehavior>>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		/**
		 * 跟Issue有关的动作的event有issuesEvent,issueCommentEvent
		 * issuesEvent中包含:
		 * 1、open一个issue
		 * 2、close一个issue
		 * 3、对此issue的指派动作
		 * 4、对issue进行label操作
		 * 5、reopen一个issue
		 * 
		 * issueCommentEvent中包含：
		 * 1、对issue的一次评论
		 * 2、对pullrequest的一个评论
		 */
		
		//取得跟issuesEvnet有关的行为
		List<Object[]> issuesEventList = session.createSQLQuery("select actor,target,createdAt,artifactId,issueaction from issuesEvent where repo='"+repo+"'").list();
		for(Object[] objects : issuesEventList ){
			Artifact<SimpleBehavior> artifact = null;
			//如果当前动作所处的Artifact是存在的，则在这个artifact中把我们的这个behavior装进去
			if(issueArtifactsMap.containsKey(StringUtil.objectToString(objects[3]))){
				artifact = issueArtifactsMap.get(StringUtil.objectToString(objects[3]));
			}else {//如果map中部存在对应的artifact,则将新new出来的对象put进map中
				artifact = new Artifact<SimpleBehavior>();
				List<SimpleBehavior> behaviors = new ArrayList<SimpleBehavior>();
				List<String> actors = new ArrayList<String>();
				artifact.setBehaviorSeq(behaviors);
				artifact.setActors(actors);
				artifact.setArtifactId(StringUtil.objectToString(objects[3]));
				issueArtifactsMap.put(StringUtil.objectToString(objects[3]), artifact);
			}
			SimpleBehavior simpleBehavior = new SimpleBehavior();
			artifact.getBehaviorSeq().add(simpleBehavior);
			/**
			 * set行为的发起者,若之前存在
			 */
			String actorString = StringUtil.objectToString(objects[0]);
			simpleBehavior.setActor(actorString);
			/**
			 * set行为的作用对象
			 */
			simpleBehavior.setTarget(StringUtil.objectToString(objects[1]));
			/**
			 * set行为的发起时间
			 */
			simpleBehavior.setCreatedAt(StringUtil.objectToString(objects[2]));
			/**
			 * 根据Event的类型和用户具体的动作，得到我们所规定的行为类型
			 */
			simpleBehavior.setEventType(BehaviorUtil.getBehaviorType("issuesEvent", StringUtil.objectToString(objects[4])));
			if(BehaviorUtil.getBehaviorType("issuesEvent", StringUtil.objectToString(objects[4]))==null){
				System.out.println("有空值");
			}
			issueArtifactsMap.put(StringUtil.objectToString(objects[3]), artifact);
		}
		
		
		//取得跟issuesCommentEvent有关的评论行为
		List<Object[]> issueCommentEventList = session.createSQLQuery("select actor,target,createdAt,artifactId from issueCommentEvent where repo='"+repo+"' and artifactId like '%issues%' order by artifactId").list();
		//handle IssueComment
		if(issueCommentEventList!=null&&issueCommentEventList.size()>0){
			
		for(Object[] objects : issueCommentEventList ){
			Artifact<SimpleBehavior> artifact = new Artifact<SimpleBehavior>();
			if(issueArtifactsMap.containsKey(StringUtil.objectToString(objects[3]))){
				artifact = issueArtifactsMap.get(StringUtil.objectToString(objects[3]));
			}else {//如果map中部存在对应的artifact,则将新new出来的对象put进map中
				artifact = new Artifact<SimpleBehavior>();
				artifact.setBehaviorSeq(new ArrayList<SimpleBehavior>());
				artifact.setArtifactId(StringUtil.objectToString(objects[3]));
				issueArtifactsMap.put(StringUtil.objectToString(objects[3]), artifact);
			}
			SimpleBehavior simpleBehavior = new SimpleBehavior();
			artifact.getBehaviorSeq().add(simpleBehavior);
			/**
			 * set行为的发起者
			 */
			String actorString = StringUtil.objectToString(objects[0]);
			simpleBehavior.setActor(actorString);
			/**
			 * set行为的作用对象
			 */
			simpleBehavior.setTarget(StringUtil.objectToString(objects[1]));
			/**
			 * set行为的发起时间
			 */
			simpleBehavior.setCreatedAt(StringUtil.objectToString(objects[2]));
			
			simpleBehavior.setEventType("issueComment");
			issueArtifactsMap.put(StringUtil.objectToString(objects[3]), artifact);
		
			}
		}
		
		/**
		 * 这里将整理出的原始artifact做一些操作，以便后来提升算法运行的速度
		 */
		for(String key:issueArtifactsMap.keySet()){
			
			Artifact artifact = issueArtifactsMap.get(key);
			/**
			 * 这里把每个artifact中的动作参与者都添加到一个list中，后面算法扫描的时候，先看待验证的行为模式中的协作者
			 * 是否完全被包含于当前的artifact中，如果完全包含，则进行下一步的验证
			 */
			
			List<SimpleBehavior> behaviorSeq = (List<SimpleBehavior>)artifact.getBehaviorSeq();
			List<String> actors = new ArrayList<String>();
			for(SimpleBehavior s:behaviorSeq){
				actors.add(s.getActor());
			}
			artifact.setActors(actors);
			issueArtifacts.add(artifact);
			
		}
		for(Artifact<SimpleBehavior> a:issueArtifacts){
			Collections.sort(a.getBehaviorSeq(),new SimpleBehaviorComparator());
		}
		
		return issueArtifacts;
	}
 
	/**
	 * 获取跟pullrequest有关的行为
	 * 跟pullrequest有关的行为包括pullrequestEvent,pullrequestReviewEvent,issueCommentEvent
	 * pullrequestEvent包含：
	 * 1、open一个pullrequest
	 * 2、close一个pullrequest
	 * 3、
	 * 4、
	 * @param repo
	 * @return
	 */
	public static List<Artifact<SimpleBehavior>> getPullrequestSimpleBehavior(String repo){
		
		return null;
	}
	/**
	 * 获取跟Commit有关系的行为
	 * @param repo
	 * @return
	 */
	public static List<Artifact<SimpleBehavior>> getCommitSimpleBehavior(String repo){
		
		return null;
	}
	
}
