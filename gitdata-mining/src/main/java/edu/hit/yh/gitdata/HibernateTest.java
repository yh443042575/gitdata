package edu.hit.yh.gitdata;


import java.util.List;

import org.hibernate.Session;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.githubDataModel.IssueCommentEvent;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

public class HibernateTest {

	@SuppressWarnings({ "unused", "unchecked" })
	public static void main(String args[]){
		Session session =  HibernateUtil.getSessionFactory().openSession();
		
		String sql = "select actor,target,htmlUrl from issuecommentevent limit 10"; 
		
		List<Object[]> behaviors = 
				session.createSQLQuery(sql).list();
		
		session.close();
		HibernateUtil.closeSessionFactory();
		System.out.println(behaviors.size());
		for(Object[] o :behaviors){
			int length = o.length;
			for(int i=0;i<length;i++){
				if(o[i]!=null){
					System.out.println(o[i].toString());					
				}else{
					System.out.println("null");
				}
			}
		}
		
		
	}
	
}
