package edu.hit.yh.gitdata.githubDataPersistence;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.hit.yh.gitdata.githubDataAnalyzer.HtmlAnalyzer;
import edu.hit.yh.gitdata.githubDataModel.CommitCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.CreateEvent;
import edu.hit.yh.gitdata.githubDataModel.DeleteEvent;
import edu.hit.yh.gitdata.githubDataModel.FollowEvent;
import edu.hit.yh.gitdata.githubDataModel.ForkEvent;
import edu.hit.yh.gitdata.githubDataModel.IssueCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.IssuesEvent;
import edu.hit.yh.gitdata.githubDataModel.MemberEvent;
import edu.hit.yh.gitdata.githubDataModel.PullRequestEvent;
import edu.hit.yh.gitdata.githubDataModel.PullRequestReviewCommentEvent;
import edu.hit.yh.gitdata.githubDataModel.PushEvent;
import edu.hit.yh.gitdata.githubDataModel.WatchEvent;


/**
 * 负责相关类的持久化操作,首先根据Type构造对应的对象， 然后通过persistenceObject将对象持久化到数据库中
 * 
 * 构造对象基本包括，行为的发起者actor,行为的内容content,行为所指向的目标target(需要后期推断),行为所在的repository
 * 
 * Message,assginee,ref都可能等于null
 * 
 * @author 程序员天猫浩
 *
 */
public class DataPersistence {

	public Object constructCommitCommentEvent(JsonObject root) {
		HtmlAnalyzer htmlAnalyzer = null;
		String htmlResult = "";
		CommitCommentEvent commitCommentEvent = new CommitCommentEvent();

		/*
		 * 得到CommitCommentEvent的payload，payload中包含comment,commit_id一类的信息
		 */
		JsonObject payload = root.getAsJsonObject("payload");
		if (payload.has("comment")) {
			JsonObject comment = payload.getAsJsonObject("comment");
			commitCommentEvent.setCommentId(comment.get("id").getAsString());
			commitCommentEvent.setCommitId(comment.get("commit_id")
					.getAsString());
			commitCommentEvent.setBody(comment.get("body").getAsString());
			commitCommentEvent
					.setHtmlUrl(comment.get("html_url").getAsString());
		} else {
			if (payload.has("comment_id"))
				commitCommentEvent.setCommentId(payload.get("comment_id")
						.getAsString());
			if (payload.has("commit_id"))
				commitCommentEvent.setCommitId(payload.get("commit_id")
						.getAsString());
		}
		if (root.has("repo")) {
			commitCommentEvent.setRepo(root.get("repo").getAsJsonObject()
					.get("name").getAsString());
		} else {
			commitCommentEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			commitCommentEvent.setActor(actor.get("login").getAsString());
		} else {
			commitCommentEvent.setActor(root.get("actor").getAsString());
		}
		if (root.has("url")) {
			commitCommentEvent.setHtmlUrl(root.get("url").getAsString());
		}
		commitCommentEvent.setCreatedAt(root.get("created_at").getAsString());
		if (commitCommentEvent.getBody().equals("")) {
			System.out.println("commitComment联网解析中...");
			htmlAnalyzer = new HtmlAnalyzer();
			htmlResult = htmlAnalyzer
					.getCommitCommentEventBodyByUrlAndCommentId(
							commitCommentEvent.getHtmlUrl(),
							commitCommentEvent.getCommentId());
			commitCommentEvent.setBody(htmlResult);
			if (!htmlResult.equals("404 not found Exception")) {
				System.out.println("联网解析成功！");
			} else {
				System.out.println("404错误，解析失败...");
			}
		}
		if (commitCommentEvent.getCommitId().equals("")) {
			if (!htmlAnalyzer.equals(null)) {
				htmlAnalyzer = new HtmlAnalyzer();
			}
			if (!htmlResult.equals("404 not found Exception"))
				htmlResult = htmlAnalyzer
						.getCommitCommentEventCommitIdByUrl(commitCommentEvent
								.getHtmlUrl());

			commitCommentEvent.setCommitId(htmlResult);
		}
		
		Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/commit/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(commitCommentEvent.getHtmlUrl());
		if(matcher.find()){
			commitCommentEvent.setArtifactId(matcher.group());
		}
		
		return commitCommentEvent;

	}

	public Object constructCreateEvent(JsonObject root) {
		CreateEvent createEvent = new CreateEvent();
		JsonObject payload = root.get("payload").getAsJsonObject();
		if (root.has("repo")) {
			createEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else {
			createEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			createEvent.setActor(actor.get("login").getAsString());
		} else {
			createEvent.setActor(root.get("actor").getAsString());
		}
		createEvent.setCreatedAt(root.get("created_at").getAsString());
		if (!payload.get("description").isJsonNull()) {
			createEvent
					.setDescription(payload.get("description").getAsString());
		}
		createEvent.setRef_type(payload.get("ref_type").getAsString());
		if (createEvent.getRef_type().equals("repository")) {
			createEvent.setRef("null");
		} else {
			createEvent.setRef(payload.get("ref").getAsString());
		}

		return createEvent;
	}

	public Object constructDeleteEvent(JsonObject root) {
		DeleteEvent deleteEvent = new DeleteEvent();
		JsonObject payload = root.get("payload").getAsJsonObject();
		if (root.has("repo")) {
			deleteEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else {
			deleteEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			deleteEvent.setActor(actor.get("login").getAsString());
		} else {
			deleteEvent.setActor(root.get("actor").getAsString());
		}
		deleteEvent.setRef(payload.get("ref").getAsString());
		deleteEvent.setRef_type(payload.get("ref_type").getAsString());
		return deleteEvent;
	}

	public Object constructForkEvent(JsonObject root) {
		ForkEvent forkEvent = new ForkEvent();
		JsonObject payload = root.get("payload").getAsJsonObject();

		if (root.has("repo")) {
			forkEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else {
			forkEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			forkEvent.setActor(actor.get("login").getAsString());
		} else {
			forkEvent.setActor(root.get("actor").getAsString());
		}
		forkEvent.setCreatedAt(root.get("created_at").getAsString());
		if (payload.has("forkee")) {
			JsonObject forkee = payload.get("forkee").getAsJsonObject();
			forkEvent.setForkeeDescription(forkee.get("description")
					.getAsString());
			forkEvent.setForkeeName(forkee.get("name").getAsString());
		} else {
			JsonObject repository = root.getAsJsonObject("repository");
			forkEvent.setForkeeOwner(repository.get("owner").getAsString());
			forkEvent.setForkeeDescription(repository.get("owner")
					.getAsString());
			forkEvent.setForkeeName(repository.get("name").getAsString());
		}

		return forkEvent;
	}

	public Object constructIssueCommentEvent(JsonObject root) {
		HtmlAnalyzer htmlAnalyzer = null;
		String htmlResult = "";
		IssueCommentEvent issueCommentEvent = new IssueCommentEvent();
		JsonObject payload = root.get("payload").getAsJsonObject();

		if (payload.has("comment")) {
			JsonObject comment = payload.get("comment").getAsJsonObject();
			issueCommentEvent.setCommentBody(comment.get("body").getAsString());

			issueCommentEvent.setCommentCreatedAt(comment.get("created_at")
					.getAsString());
			issueCommentEvent.setCommentId(comment.get("id").getAsString());
			issueCommentEvent.setCommentUser(comment.getAsJsonObject("user")
					.get("login").getAsString());
		} else if (payload.has("comment_id")) {
			issueCommentEvent.setCommentId(payload.get("comment_id")
					.getAsString());

		}
		if (payload.has("issue")) {
			JsonObject issue = payload.getAsJsonObject("issue");
			issueCommentEvent.setHtmlUrl(issue.get("html_url").getAsString());
			issueCommentEvent.setIssueAssingee(issue.get("assignee")
					.isJsonNull() ? "null" : issue.getAsJsonObject("assignee")
					.get("login").getAsString());
			issueCommentEvent.setIssueCreatedAt(issue.get("created_at")
					.getAsString());
			issueCommentEvent.setIssueLabels(issue.get("labels")
					.getAsJsonArray().size() == 0 ? "" : "1");
			issueCommentEvent.setIssueUser(issue.getAsJsonObject("user")
					.get("login").getAsString());
		} else if (payload.has("issue_id")) {
			issueCommentEvent.setHtmlUrl(root.get("url").getAsString());
			issueCommentEvent.setIssueId(payload.get("issue_id").getAsString());
		}

		if (root.has("repo")) {
			issueCommentEvent.setRepo(root.get("repo").getAsJsonObject()
					.get("name").getAsString());
		} else {
			issueCommentEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			issueCommentEvent.setActor(actor.get("login").getAsString());
		} else {
			issueCommentEvent.setActor(root.get("actor").getAsString());
		}

		issueCommentEvent.setCreatedAt(root.get("created_at").getAsString());

		if (issueCommentEvent.getCommentBody() == null
				|| issueCommentEvent.getCommentBody().equals("")) {
			System.out.println("issueCommentEvent联网解析中..."+" "+issueCommentEvent.getHtmlUrl());
			htmlAnalyzer = new HtmlAnalyzer();
			htmlResult = htmlAnalyzer
					.getIssueCommentEventBodyByUrlAndCommentId(
							issueCommentEvent.getHtmlUrl(),
							issueCommentEvent.getCommentId());
			issueCommentEvent.setCommentBody(htmlResult);
			if (!htmlResult.equals("404 not found Exception")) {
				System.out.println("联网解析成功！");
			} else {
				System.out.println("404错误，解析失败...");
			}
		} else {
			System.out.println("issueCommentEvent不为空，内容为："
					+ issueCommentEvent.getCommentBody());

		}

		Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/issues/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(issueCommentEvent.getHtmlUrl());
		if(matcher.find()){
			issueCommentEvent.setArtifactId(matcher.group());
		}
		
		return issueCommentEvent;

	}

	public Object constructIssuesEvent(JsonObject root) {
		IssuesEvent issuesEvent = new IssuesEvent();
		System.out.println("2013issuesEvent示例："+root.toString());
		if (root.has("repo")) {
			issuesEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else {
			issuesEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			issuesEvent.setActor(actor.get("login").getAsString());
		} else {
			issuesEvent.setActor(root.get("actor").getAsString());
		}
		JsonObject payload = root.get("payload").getAsJsonObject();
		if (payload.get("issue").isJsonObject()) {
			JsonObject issue = payload.getAsJsonObject("issue");
			issuesEvent
					.setIssueAssingee(issue.get("assignee").isJsonNull() ? "null"
							: issue.getAsJsonObject("assignee").get("login")
									.getAsString());
			JsonArray labels = issue.get("labels").getAsJsonArray();
			if (labels.size() == 0) {
				issuesEvent.setIssueLabels("");
			} else if (labels.get(0).isJsonObject()) {
				StringBuilder stringBuilder = new StringBuilder("");
				for (JsonElement j : labels) {
					stringBuilder.append(j.getAsJsonObject().get("name") + ",");
				}
				stringBuilder.deleteCharAt(stringBuilder.length() - 1);
				issuesEvent.setIssueLabels(stringBuilder.toString());
			} else {
				issuesEvent.setIssueLabels(issue.get("labels").getAsString());
			}
			issuesEvent
					.setIssueCreatedAt(issue.get("created_at").getAsString());
			issuesEvent.setIssueUser(issue.getAsJsonObject("user").get("login")
					.getAsString());
		} else {

			issuesEvent.setIssueId(payload.get("issue").getAsString());
		}

		issuesEvent.setCreatedAt(root.get("created_at").getAsString());
		issuesEvent.setIssueAction(payload.get("action").getAsString());
		
		/*Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/issues/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(issuesEvent.get);
		if(matcher.find()){
			issueCommentEvent.setArtifactId(matcher.group());
		}*/
		
		
		
		return issuesEvent;
	}

	public Object constructMemberEvent(JsonObject root) {
		MemberEvent memberEvent = new MemberEvent();
		if (root.has("repo")) {
			memberEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else {
			memberEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			memberEvent.setActor(actor.get("login").getAsString());
		} else {
			memberEvent.setActor(root.get("actor").getAsString());
		}
		JsonObject payload = root.get("payload").getAsJsonObject();

		memberEvent.setAction(payload.get("action").getAsString());
		memberEvent.setCreatedAt(root.get("created_at").getAsString());
		memberEvent.setMember(payload.getAsJsonObject("member").get("login")
				.getAsString());
		return memberEvent;

	}

	public Object constructPullRequestEvent(JsonObject root) {
		PullRequestEvent pullrequestEvent = new PullRequestEvent();
		if (root.has("repo")) {
			pullrequestEvent.setRepo(root.get("repo").getAsJsonObject()
					.get("name").getAsString());
		} else {
			pullrequestEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			pullrequestEvent.setActor(actor.get("login").getAsString());
		} else {
			pullrequestEvent.setActor(root.get("actor").getAsString());
		}
		JsonObject payload = root.get("payload").getAsJsonObject();
		JsonObject pullrequest = payload.getAsJsonObject("pull_request");
		JsonObject base = pullrequest.getAsJsonObject("base");
		JsonObject head = pullrequest.getAsJsonObject("head");

		pullrequestEvent.setAction(payload.get("action").getAsString());
		pullrequestEvent.setBody(pullrequest.get("body").getAsString());
		pullrequestEvent.setCreatedAt(root.get("created_at").getAsString());
		if (pullrequest.has("assignee"))
			pullrequestEvent.setPullrequestAssgnee(pullrequest.get("assignee")
					.isJsonNull() ? "null" : pullrequest
					.getAsJsonObject("assignee").get("login").getAsString());
		pullrequestEvent.setPullrequestBaseLabels(base.get("label")
				.getAsString());
		pullrequestEvent.setPullrequestBaseRef(base.get("ref").getAsString());
		pullrequestEvent.setPullrequestBaseSha(base.get("sha").getAsString());
		pullrequestEvent.setPullrequestBaseUser(base.getAsJsonObject("user")
				.get("login").getAsString());
		pullrequestEvent.setPullrequestBody(pullrequest.get("body")
				.getAsString());
		pullrequestEvent.setPullrequestDiffUrl(pullrequest.get("diff_url")
				.getAsString());
		pullrequestEvent.setPullrequestHeadLabels(head.get("label")
				.getAsString());
		pullrequestEvent.setPullrequestHeadRef(head.get("ref").getAsString());
		pullrequestEvent.setPullrequestHeadSha(head.get("sha").getAsString());
		pullrequestEvent.setPullrequestHeadUser(head.getAsJsonObject("user")
				.get("login").getAsString());
		pullrequestEvent.setPullrequestHtmlUrl(pullrequest.get("html_url")
				.getAsString());
		pullrequestEvent.setPullrequestId(pullrequest.get("id").getAsString());

		Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/pull/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(pullrequestEvent.getPullrequestHtmlUrl());
		if(matcher.find()){
			pullrequestEvent.setArtifactId(matcher.group());
		}
		
		return pullrequestEvent;
		
	}

	public Object constructPullRequestReviewCommentEvent(JsonObject root) {
		System.out.println("PullRequestReviewComment" + root.toString());
		PullRequestReviewCommentEvent pullRequestReviewCommentEvent = new PullRequestReviewCommentEvent();
		JsonObject payload = root.get("payload").getAsJsonObject();
		JsonObject comment = payload.getAsJsonObject("comment");

		if (root.has("repo")) {
			pullRequestReviewCommentEvent.setRepo(root.get("repo")
					.getAsJsonObject().get("name").getAsString());
		} else {
			pullRequestReviewCommentEvent.setRepo(root.get("repository")
					.getAsJsonObject().get("name").getAsString());
		}

		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			pullRequestReviewCommentEvent.setActor(actor.get("login")
					.getAsString());
		} else {
			pullRequestReviewCommentEvent.setActor(root.get("actor")
					.getAsString());
		}

		pullRequestReviewCommentEvent.setCommentBody(comment.get("body")
				.getAsString());
		pullRequestReviewCommentEvent.setCreatedAt(root.get("created_at")
				.getAsString());

		// 如果有url则直接将url写到对象中，如果没有，则再次发送http请求，去取得该pullrequest所对应的html_url
		if (root.has("url")) {
			pullRequestReviewCommentEvent.setHtmlUrl(root.get("url")
					.getAsString());
			System.out.println("自带url..."+root.get("url").getAsString());
		} else {
			HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
			try {
				//将http请求回来的数据转化成json
				JsonObject pullrequestJson = (JsonObject) new JsonParser()
						.parse(htmlAnalyzer.getResource(comment.get("url")
								.getAsString()));
				System.out.println("请求得url..."+pullrequestJson.get("pull_request_url").getAsString());
				pullRequestReviewCommentEvent.setHtmlUrl(pullrequestJson.get("pull_request_url").getAsString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Pattern artifactPattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/pull/[a-z[0-9]]+");
		Matcher matcher = artifactPattern.matcher(pullRequestReviewCommentEvent.getHtmlUrl());
		if(matcher.find()){
			pullRequestReviewCommentEvent.setArtifactId(matcher.group());
		}
		return pullRequestReviewCommentEvent;
	}

	public Object constructPushEvent(JsonObject root) {

		PushEvent pushEvent = new PushEvent();
		JsonObject payload = root.get("payload").getAsJsonObject();
		JsonObject lastCommit = null;
		if (root.has("repo")) {
			pushEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else {
			pushEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			pushEvent.setActor(actor.get("login").getAsString());
		} else {
			pushEvent.setActor(root.get("actor").getAsString());
		}
		pushEvent.setHead(payload.get("head").getAsString());
		pushEvent.setRef(payload.get("ref").getAsString());
		pushEvent.setCreatedAt(root.get("created_at").getAsString());
		if (payload.has("commits")) {
			JsonArray commits = payload.get("commits").getAsJsonArray();
			if (commits.size() > 0) {
				lastCommit = payload.get("commits").getAsJsonArray()
						.get(commits.size() - 1).getAsJsonObject();
				if (lastCommit.get("sha").getAsString()
						.equals(payload.get("head").getAsString()))
					pushEvent.setCommitMessage(lastCommit.get("message")
							.getAsString());
				pushEvent.setCommitSha(lastCommit.get("sha").getAsString());
			}
		}
		if (pushEvent.getCommitMessage().equals("")) {
			// System.out.println(root.toString());
		}
		return pushEvent;
	}

	public Object constructWatchEvent(JsonObject root) {
		WatchEvent watchEvent = new WatchEvent();
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			watchEvent.setActor(actor.get("login").getAsString());
		} else {
			watchEvent.setActor(root.get("actor").getAsString());
		}
		if (root.has("repo")) {
			watchEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else {
			watchEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		JsonObject payload = root.get("payload").getAsJsonObject();
		watchEvent.setAction(payload.get("action").getAsString());
		watchEvent.setCreatedAt(root.get("created_at").getAsString());

		return watchEvent;
	}

	public Object constructFollowEvent(JsonObject root) {
		FollowEvent followEvent = new FollowEvent();
		JsonObject target = root.getAsJsonObject("payload").getAsJsonObject(
				"target");
		if (root.get("actor").isJsonObject()) {
			JsonObject actor = root.get("actor").getAsJsonObject();
			followEvent.setActor(actor.get("login").getAsString());
		} else {
			followEvent.setActor(root.get("actor").getAsString());
		}
		if (root.has("repo")) {
			followEvent.setRepo(root.get("repo").getAsJsonObject().get("name")
					.getAsString());
		} else if (root.has("repository")) {
			followEvent.setRepo(root.get("repository").getAsJsonObject()
					.get("name").getAsString());
		}
		followEvent.setTarget(target.get("login").getAsString());

		return followEvent;
	}
	
	/*
	 * 通过开发者每一次行为的谈话内容，识别出该行为是针对谁的
	 */
	private String findTargetByContent(String content){
		
		StringBuffer target = new StringBuffer("");
		Pattern pattern = Pattern.compile("@[a-zA-Z]+");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			target.append(matcher.group()+"+");
		}
		return target.toString();
	}
	
}
