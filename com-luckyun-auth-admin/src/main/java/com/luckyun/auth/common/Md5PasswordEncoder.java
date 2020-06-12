package com.luckyun.auth.common;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.luckyun.auth.util.Md5Utils;


/**
 * MD5加密密码策略实现
 * 2019年01月15日,下午13:49
 * {@link com.lucksoft.auth.common.Md5PasswordEncoder}
 * @author yangj080
 *
 */
public class Md5PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return Md5Utils.md5((String) rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encodedPassword.equals(Md5Utils.md5((String) rawPassword));
	}

}
