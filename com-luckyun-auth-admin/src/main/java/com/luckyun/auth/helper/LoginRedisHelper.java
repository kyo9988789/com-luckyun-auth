package com.luckyun.auth.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.luckyun.core.redis.RedisOperationDao;

@Component
public class LoginRedisHelper {

	@Autowired
	private RedisOperationDao redisOperationDao;
	
	/**
	 * 设置当前登录人登录的token信息
	 * @param username 用户登录名
	 * @param token token信息
	 */
	public void setCurrentUserToken(String username,String token) {
		redisOperationDao.set("auth_user_login_token_record_" + username, token);
		//设置3的超时时间
		redisOperationDao.expire("auth_user_login_token_record_" + username, 259200L);
	}
	
	public String getCurrentUserToken(String username) {
		return redisOperationDao.get("auth_user_login_token_record_" + username);
	}
	
	public Long getExpireKey(String username) {
		return redisOperationDao.getExpireTime("auth_user_login_token_record_" + username);
	}
	
	public void setLoginidIcompanyId(String username,Long icompanyId) {
		if(icompanyId != null) {
			redisOperationDao.hSet("auth_login_hash_icompanyid",username, icompanyId.toString());
		}
	}
	
	public Long getLoginIcompanyId(String username) {
		String value = redisOperationDao.hMget("auth_login_hash_icompanyid", username);
		if(!StringUtils.isEmpty(value)) {
			return Long.valueOf(value);
		}
		return null;
	}
}
