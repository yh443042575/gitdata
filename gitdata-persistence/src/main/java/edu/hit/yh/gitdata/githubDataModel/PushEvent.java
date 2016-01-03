package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * commit代码的操作
 * @author DHAO
 *
 */
@Entity
public class PushEvent {
	
	private long id;
	private String actor;
	private String repo;
	
	private String pushId;
	
	private String before;
	/**
	 * The SHA of the HEAD commit on the repository.
	 */
	private String head;
	/**
	 * The full Git ref that was pushed. Example: “refs/heads/master”
	 */
	private String ref;
	/**
	 * 本次commit时所附带的message
	 */
	private String commitSha;
	private String commitMessage = "";
	
	private String createdAt;
	private String target;
	private String artifactId;
	@Getter
	@Setter
	private String htmlUrl;
	@Getter
	@Setter
	private String title;
	@Getter
	@Setter
	private String sourceType;
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
	public String getPushId() {
		return pushId;
	}
	public void setPushId(String pushId) {
		this.pushId = pushId;
	}
	@Column(name="`before`")
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getCommitMessage() {
		return commitMessage;
	}
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
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
	public String getCommitSha() {
		return commitSha;
	}
	public void setCommitSha(String commitSha) {
		this.commitSha = commitSha;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	
}
