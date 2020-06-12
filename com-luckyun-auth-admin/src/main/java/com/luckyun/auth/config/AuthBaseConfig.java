package com.luckyun.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 获取基本的配置文件内容
 * @author yj
 *
 */
@Configuration
@ConfigurationProperties(prefix = "common")
@Data
public class AuthBaseConfig {
	
	/**
	 * 开发模式
	 */
	private String devMode;
	
	/**
	 * 放行用户
	 */
	private String releaseUser;
	
	/**
	 * 是否单点登录
	 */
	@Value("${common.is-single-login:false}")
	private boolean isSingleLogin;
	
	@Value("${common.no-interceptor.container:/bpm}")
	private String noInterceptorContainer;
	
	@Value("${common.no-interceptor.startwith:noAuth}")
	private String noInterceptorStartwith;
	
	@Value("${common.no-interceptor.endwith:noAuth}")
	private String noInterceptorEndwith;
}
