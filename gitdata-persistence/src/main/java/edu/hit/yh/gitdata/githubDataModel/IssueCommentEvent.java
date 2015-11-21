package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class IssueCommentEvent {
	
	private long id;
	private String actor;
	private String repo;
	private String htmlUrl;
	private String issueId;
	private String issueUser;
	private String issueLabels;
	private String issueAssingee;
	private String issueCreatedAt;
	private String commentId;
	private String commentUser;
	private String commentBody;
	private String commentCreatedAt;
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
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	public String getIssueUser() {
		return issueUser;
	}
	public void setIssueUser(String issueUser) {
		this.issueUser = issueUser;
	}
	public String getIssueLabels() {
		return issueLabels;
	}
	public void setIssueLabels(String issueLabels) {
		this.issueLabels = issueLabels;
	}
	public String getIssueAssingee() {
		return issueAssingee;
	}
	public void setIssueAssingee(String issueAssingee) {
		this.issueAssingee = issueAssingee;
	}
	public String getIssueCreatedAt() {
		return issueCreatedAt;
	}
	public void setIssueCreatedAt(String issueCreatedAt) {
		this.issueCreatedAt = issueCreatedAt;
	}
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getCommentUser() {
		return commentUser;
	}
	public void setCommentUser(String commentUser) {
		this.commentUser = commentUser;
	}
	public String getCommentBody() {
		return commentBody;
	}
	public void setCommentBody(String commentBody) {
		this.commentBody = commentBody;
	}
	public String getCommentCreatedAt() {
		return commentCreatedAt;
	}
	public void setCommentCreatedAt(String commentCreatedAt) {
		this.commentCreatedAt = commentCreatedAt;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getHtmlUrl() {
		return htmlUrl;
	}
	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	
	
	
	
	
}
