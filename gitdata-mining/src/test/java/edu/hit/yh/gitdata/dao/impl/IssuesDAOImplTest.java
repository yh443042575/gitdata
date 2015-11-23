package edu.hit.yh.gitdata.dao.impl;

import edu.hit.yh.gitdata.dao.IssuesDAO;
import junit.framework.TestCase;

public class IssuesDAOImplTest extends TestCase {

	
	public void testGetIssuesEvents() {
		IssuesDAO issuesDAOImpl = new IssuesDAOImpl();
		issuesDAOImpl.getIssuesEvents("jquery/jquery");
		
	}

	public void testGetIssueCommentEvent() {
	}

}
