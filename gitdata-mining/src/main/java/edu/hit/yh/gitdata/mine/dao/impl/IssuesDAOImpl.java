package edu.hit.yh.gitdata.mine.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

/**
 * 做对操作Issue有关的所有操作
 * @author DHAO
 *
 */
public class IssuesDAOImpl /*implements IssuesDAO*/ {

	@SuppressWarnings("unused")
	public List<SimpleBehavior> getIssuesEvents(String repo) {
		// TODO 取出所给repo中的所有IssuesEvent
		Session session = HibernateUtil.getSessionFactory().openSession();
		@SuppressWarnings("unchecked")
		List<Object[]> list = session.createSQLQuery("select * from issuecommentevent where repo='"+repo+"'").list();
		for(Object[] oo:list){
			for(Object obj:oo){
				if(obj == null){
					System.out.print("null||");
				}else {
					System.out.print(obj.toString()+"||");
				}
			}
			System.out.println();
		}
		return null;
	}

	public List<SimpleBehavior> getIssueCommentEvent(String repo) {
		// TODO 取出所给repo中的所有IssueCommentEvent
		return null;
	}

}
