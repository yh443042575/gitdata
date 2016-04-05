package edu.hit.yh.gitdata.grphmodel;

import lombok.Data;

/**
 * 生成图像所需要的点
 * @author DHAO
 *
 */
@Data
public class ShannonWiener {

	/**
	 * 横坐标：支持度
	 */
	Integer surpport;
	/**
	 * 纵坐标：多样性指数
	 */
	Double diversity;
	
}
