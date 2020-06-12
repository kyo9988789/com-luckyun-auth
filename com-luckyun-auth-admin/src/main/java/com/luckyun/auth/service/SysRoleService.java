package com.luckyun.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luckyun.auth.mapper.SysRoleMapper;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.core.data.BaseService;
import com.luckyun.model.role.SysRole;

@Service
public class SysRoleService extends BaseService<SysRole>{

	@Autowired
	private SysRoleMapper sysRoleMapper;
	
	@Override
	public BaseMapper<SysRole> getMapper() {
		return sysRoleMapper;
	}

}
