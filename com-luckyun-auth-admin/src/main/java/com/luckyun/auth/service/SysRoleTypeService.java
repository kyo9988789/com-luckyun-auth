package com.luckyun.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luckyun.auth.mapper.SysRoleTypeMapper;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.core.data.BaseService;
import com.luckyun.model.role.SysRoleType;

@Service
public class SysRoleTypeService extends BaseService<SysRoleType>{

	@Autowired
	private SysRoleTypeMapper sysRoleTypeMapper;
	
	@Override
	public BaseMapper<SysRoleType> getMapper() {
		return sysRoleTypeMapper;
	}

}
