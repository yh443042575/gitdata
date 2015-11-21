package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ForkEvent {

	private long id;
	private String actor;
	/**
	 * fork人家的代码的repository
	 */
	private String repo;
	
	/**
	 * fork到自己仓库时新建的repository信息
	 */
	private String forkeeName;
	private String forkeeOwner;
	private String forkeeDescription;
	
	private String createdAt;
	private String target;
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
	public String getForkeeName() {
		return forkeeName;
	}
	public void setForkeeName(String forkeeName) {
		this.forkeeName = forkeeName;
	}
	public String getForkeeOwner() {
		return forkeeOwner;
	}
	public void setForkeeOwner(String forkeeOwner) {
		this.forkeeOwner = forkeeOwner;
	}
	public String getForkeeDescription() {
		return forkeeDescription;
	}
	public void setForkeeDescription(String forkeeDescription) {
		this.forkeeDescription = forkeeDescription;
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
	
	
}
