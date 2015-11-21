package edu.hit.yh.gitdata.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import edu.hit.yh.gitdata.dao.IssuesDAO;
import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

public class IssuesDAOImpl implements IssuesDAO {

	
	
	public List<SimpleBehavior> getIssuesEvents(String repo) {
		// TODO 取出所给repo中的所有IssuesEvent
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.createSQLQuery("");
		return null;
	}

	public List<SimpleBehavior> getIssueCommentEvent(String repo) {
		// TODO 取出所给repo中的所有IssuesEvent
		return null;
	}

}
