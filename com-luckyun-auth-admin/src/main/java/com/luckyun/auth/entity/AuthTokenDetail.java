package com.luckyun.auth.entity;

import lombok.Data;

@Data
public class AuthTokenDetail {

	/**
	 * key的过期时间
	 */
	private Long expire;
}
