package edu.hit.yh.gitdata.mine.util;

import java.util.List;

import org.hibernate.Session;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.githubDataModel.IssuesEvent;

/**
 * 用来修复数据库的类
 * @author DHAO
 *
 */
public class DataRepairUtil {

	public static void repairNullActorForIssueEvent(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<IssuesEvent> issuesEventList = session.createSQLQuery("SELECT * from issuesevent where actor =''").addEntity(IssuesEvent.class).list();
		System.out.println(issuesEventList.size());
		
		session.beginTransaction();
		for(IssuesEvent i:issuesEventList){
			List<String> owner = session.createSQLQuery("SELECT `owner` from artifactowner where artifactId = '"+i.getIssueUrl()+"'").list();
			i.setActor(owner.get(0));
			session.update(i);
			System.out.println(owner.get(0));
		}
		session.getTransaction().commit();
		
		HibernateUtil.closeSessionFactory();
		
	}
	
	public static void main(String args[]){
		DataRepairUtil.repairNullActorForIssueEvent();
	}
	
	
	
}
