package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * 删除事件
 * @author DHAO
 *
 */
@Entity
public class DeleteEvent {
	
	private long id;
	private String actor;
	private String repo;
	/**
	 * ref_type只能是branch,tag
	 */
	private String ref_type;
	/**
	 * ref_type中对应的具体名字
	 */
	private String ref;
	private String target;
	/*
	 * 何时做的删除
	 */
	@Getter
	@Setter
	private String deleteAt;
	
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
	public String getRef_type() {
		return ref_type;
	}
	public void setRef_type(String ref_type) {
		this.ref_type = ref_type;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	
	
	
	
	
}
