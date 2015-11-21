package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/*
 * 与pullrequest相关的事件
 */
@Entity
public class PullRequestEvent {

	private long id;
	private String actor;
	private String repo;
	private String action;
	private String body;
	private String pullrequestId;
	private String pullrequestDiffUrl;
	private String pullrequestHtmlUrl;
	private String pullrequestBody;
	private String pullrequestAssgnee;
	private String pullrequestHeadSha;
	private String pullrequestHeadLabels;
	private String pullrequestHeadRef;
	private String pullrequestHeadUser;
	private String pullrequestBaseSha;
	private String pullrequestBaseLabels;
	private String pullrequestBaseRef;
	private String pullrequestBaseUser;
	private String createdAt;
	private String target;
	private String artifactId;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getPullrequestId() {
		return pullrequestId;
	}
	public void setPullrequestId(String pullrequestId) {
		this.pullrequestId = pullrequestId;
	}
	public String getPullrequestDiffUrl() {
		return pullrequestDiffUrl;
	}
	public void setPullrequestDiffUrl(String pullrequestDiffUrl) {
		this.pullrequestDiffUrl = pullrequestDiffUrl;
	}
	public String getPullrequestHtmlUrl() {
		return pullrequestHtmlUrl;
	}
	public void setPullrequestHtmlUrl(String pullrequestHtmlUrl) {
		this.pullrequestHtmlUrl = pullrequestHtmlUrl;
	}
	public String getPullrequestBody() {
		return pullrequestBody;
	}
	public void setPullrequestBody(String pullrequestBody) {
		this.pullrequestBody = pullrequestBody;
	}
	public String getPullrequestAssgnee() {
		return pullrequestAssgnee;
	}
	public void setPullrequestAssgnee(String pullrequestAssgnee) {
		this.pullrequestAssgnee = pullrequestAssgnee;
	}
	public String getPullrequestHeadSha() {
		return pullrequestHeadSha;
	}
	public void setPullrequestHeadSha(String pullrequestHeadSha) {
		this.pullrequestHeadSha = pullrequestHeadSha;
	}
	public String getPullrequestHeadLabels() {
		return pullrequestHeadLabels;
	}
	public void setPullrequestHeadLabels(String pullrequestHeadLabels) {
		this.pullrequestHeadLabels = pullrequestHeadLabels;
	}
	public String getPullrequestHeadRef() {
		return pullrequestHeadRef;
	}
	public void setPullrequestHeadRef(String pullrequestHeadRef) {
		this.pullrequestHeadRef = pullrequestHeadRef;
	}
	public String getPullrequestHeadUser() {
		return pullrequestHeadUser;
	}
	public void setPullrequestHeadUser(String pullrequestHeadUser) {
		this.pullrequestHeadUser = pullrequestHeadUser;
	}
	public String getPullrequestBaseSha() {
		return pullrequestBaseSha;
	}
	public void setPullrequestBaseSha(String pullrequestBaseSha) {
		this.pullrequestBaseSha = pullrequestBaseSha;
	}
	public String getPullrequestBaseLabels() {
		return pullrequestBaseLabels;
	}
	public void setPullrequestBaseLabels(String pullrequestBaseLabels) {
		this.pullrequestBaseLabels = pullrequestBaseLabels;
	}
	public String getPullrequestBaseRef() {
		return pullrequestBaseRef;
	}
	public void setPullrequestBaseRef(String pullrequestBaseRef) {
		this.pullrequestBaseRef = pullrequestBaseRef;
	}
	public String getPullrequestBaseUser() {
		return pullrequestBaseUser;
	}
	public void setPullrequestBaseUser(String pullrequestBaseUser) {
		this.pullrequestBaseUser = pullrequestBaseUser;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	
	
	
	
	
	
	
	
}
