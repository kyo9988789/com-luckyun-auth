package com.luckyun.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luckyun.auth.mapper.SysModuleMapper;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.core.data.BaseService;
import com.luckyun.model.module.SysModule;

@Service
public class SysModuleServive extends BaseService<SysModule>{

	@Autowired
	private SysModuleMapper sysModuleMapper;
	
	@Override
	public BaseMapper<SysModule> getMapper() {
		return sysModuleMapper;
	}

}
