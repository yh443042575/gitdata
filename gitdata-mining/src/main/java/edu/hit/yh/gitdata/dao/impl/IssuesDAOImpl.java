package edu.hit.yh.gitdata.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import edu.hit.yh.gitdata.dao.IssuesDAO;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

public class IssuesDAOImpl implements IssuesDAO {

	
	
	@SuppressWarnings("unused")
	public List<SimpleBehavior> getIssuesEvents(String repo) {
		// TODO 取出所给repo中的所有IssuesEvent
		Session session = HibernateUtil.getSessionFactory().openSession();
		@SuppressWarnings("unchecked")
		List<Object[]> list = session.createSQLQuery("select * from issuecommentevent where repo='"+repo+"'").list();
		
		return null;
	}

	public List<SimpleBehavior> getIssueCommentEvent(String repo) {
		// TODO 取出所给repo中的所有IssueCommentEvent
		return null;
	}

}
