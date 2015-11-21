package edu.hit.yh.gitdata.dao;

import java.util.List;

import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

/**
 * 针对pullrequest的数据库接口
 * @author DHAO
 *
 */
public interface IssuesDAO {

	public List<SimpleBehavior> getIssuesEvents(String repo);
	
	public List<SimpleBehavior> getIssueCommentEvent(String repo);
	
}
