package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class PullRequestReviewCommentEvent {

	private long id;
	private String actor;
	private String repo;
	private String commentBody;
	/*Comment本身的html,包含disscussion序号的，如https://github.com/jquery/jquery/pull/1117#discussion_r2567843*/
	private String htmlUrl;
	/*
	 * 规定pullrequestId为 repo+pull序号
	 */
	private String pullrequestId;
	private String createdAt;
	private String target;
	private String artifactId;
	
	@Getter
	@Setter
	private String discussionId;
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
	public String getCommentBody() {
		return commentBody;
	}
	public void setCommentBody(String commentBody) {
		this.commentBody = commentBody;
	}
	public String getPullrequestId() {
		return pullrequestId;
	}
	public void setPullrequestId(String pullrequestId) {
		this.pullrequestId = pullrequestId;
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
