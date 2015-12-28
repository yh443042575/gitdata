package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;


@Entity
public class IssuesEvent {
	private long id;
	private String actor;
	private String repo;
	private String issueId;
	private String issueAction;
	private String issueUser;
	private String issueLabels;
	private String issueAssingee;
	private String issueCreatedAt;
	
	private String issueUrl;
	private String createdAt;
	private String artifactId;
	@Getter
	@Setter
	private String desContent1;
	@Getter
	@Setter
	private String issueContent;
	@Getter
	@Setter
	private String SourceType;
	
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
	public String getIssueAction() {
		return issueAction;
	}
	public void setIssueAction(String issueAction) {
		this.issueAction = issueAction;
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
	public String getIssueUrl() {
		return issueUrl;
	}
	public void setIssueUrl(String issueUrl) {
		this.issueUrl = issueUrl;
	}
	
	
	
	
}
