package com.luckyun.auth.service;

import com.luckyun.auth.entity.SysUserLogin;

/**
 * 
 * @author yangj080
 *
 */
public interface AuthSysUserLoginService {

	/**
	 *添加用户登录日志信息
	 * @param sysUserLogin 用户登录的日志信息
	 * @return 用户登录后的日志保存信息
	 */
	SysUserLogin add(SysUserLogin sysUserLogin);
}
