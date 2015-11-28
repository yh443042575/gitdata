package edu.hit.yh.gitdata.mine.dao;

import java.util.List;

import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

public interface IssueCommentEventDAO {

	
	public List<SimpleBehavior> queryIssueCommentEventByRepo(String repo);
}
