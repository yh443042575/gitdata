package edu.hit.yh.gitdata.mine.util;

import java.util.HashMap;
import java.util.Map;

public class BehaviorUtil {
	private static Map<String, String> behaviorTypeMap = new HashMap<String, String>();
	/**
	 * 将Event类型和细节动作合在一起，得到我们所规定的行为
	 */
	static{
		
		behaviorTypeMap.put("commitCommentEvent", "commitComment");
		behaviorTypeMap.put("pushEvent", "commit");
		behaviorTypeMap.put("issueCommentEvent", "issueOrPullrequestComment");
		behaviorTypeMap.put("issueEvent open", "openIssue");
		behaviorTypeMap.put("issueEvent closed", "closeIssue");
		behaviorTypeMap.put("issueEvent reopen", "reopenIssue");
		behaviorTypeMap.put("issueEvent assigned", "assignedSomeone");
		behaviorTypeMap.put("issueEvent unassigned", "unAssignedSomeone");
		behaviorTypeMap.put("issueEvent labeled", "addLable");
		behaviorTypeMap.put("issueEvent unlabeled", "deleteLable");
		behaviorTypeMap.put("pullrequestEvent open", "openPullrequest");
		behaviorTypeMap.put("pullrequestEvent closed", "closePullrequest");
		behaviorTypeMap.put("pullrequestEvent reopen", "reopenPullrequest");
		behaviorTypeMap.put("pullRequestReviewCommentEvent", "pullRequestReviewComment");
		behaviorTypeMap.put("deleteEvent", "deleteBranch");
		behaviorTypeMap.put("watchEvent", "watch");
		behaviorTypeMap.put("forkEvent", "fork");
		
		
	}
	
	/**
	 * 根据类型和细节，得到用户此次行为的
	 * @param EventType
	 * @param detail
	 * @return
	 */
	public static String getBehaviorType(String EventType,String detail){
		
		return behaviorTypeMap.get(EventType+" "+detail);
		
	}
	
	
}
