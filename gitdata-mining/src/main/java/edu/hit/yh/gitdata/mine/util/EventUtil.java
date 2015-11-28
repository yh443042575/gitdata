package edu.hit.yh.gitdata.mine.util;

import edu.hit.yh.gitdata.githubDataModel.IssueCommentEvent;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;


public class EventUtil {

	public static SimpleBehavior transIssueCommentEventToSimpleBehavior(IssueCommentEvent issueCommentEvent){
		
		SimpleBehavior simpleBehavior = new SimpleBehavior();
		
		simpleBehavior.setActor(issueCommentEvent.getActor());
		simpleBehavior.setEventType("issueCommentEvent");
		simpleBehavior.setTarget(issueCommentEvent.getTarget());
		
		return simpleBehavior;
		
		
		
	}
	
}
