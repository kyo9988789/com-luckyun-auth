package com.luckyun.auth.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * md5辅助类
 * @author yangj080
 *
 */
public class Md5Utils {

	static MessageDigest md = null;

	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ne) {
			ne.printStackTrace();
		}
	}

	/**
	 * 求一个字符串的md5值
	 * 
	 * @param target 字符串
	 * @return md5 value
	 */
	public static String md5(String target) {
		return DigestUtils.md5Hex(target);
	}

	/***
	 * 根据字符串生成 32位的 MD5 码
	 * 
	 * @author tmc.sun 2012-11-05
	 * @param str 待生成 MD5码的字符串
	 * @return 根据字符串生成的 MD5码
	 */
	public static String stringToMD5(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}

		MessageDigest md5 = null;
		StringBuffer value = new StringBuffer();

		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] md5Bytes = md5.digest(str.getBytes("utf-8"));
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = (md5Bytes[i]) & 0xff;
				if (val < 16) {
					value.append("0");
				}
				value.append(Integer.toHexString(val));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return value.toString();
	}
}
