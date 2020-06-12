package com.luckyun.auth.entity;

import lombok.Data;

@Data
public class UpdatePwdEntity {

	private String oldPwd;
	
	private String newPwd;
}
