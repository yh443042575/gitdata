package edu.hit.yh.gitdata.githubDataModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取一个repo的url的实体类
 * @author DHAO
 *
 */
@Entity
public class CommitUrls {

	private long id;
	@Getter
	@Setter
	private String repo;
	@Getter
	@Setter
	private String url;
	@Getter
	@Setter
	private String title;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
