package com.luckyun.auth.entity.common;

import java.io.Serializable;

import lombok.Data;

/**
 * 登录数据接收实体
 * @author yangj080
 *
 */
@Data
public class LoginEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**用户名称*/
	private String username;
	/**登录密码*/
	private String password;
	/**所属公司*/
	private Long icompanyid;
}
