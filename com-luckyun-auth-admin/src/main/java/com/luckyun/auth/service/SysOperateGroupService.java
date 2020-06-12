package com.luckyun.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luckyun.auth.mapper.SysOperateGroupMapper;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.core.data.BaseService;
import com.luckyun.model.operate.SysOperateGroup;

@Service
public class SysOperateGroupService extends BaseService<SysOperateGroup>{

	@Autowired
	private SysOperateGroupMapper sysOperateGroupMapper;
	
	@Override
	public BaseMapper<SysOperateGroup> getMapper() {
		return sysOperateGroupMapper;
	}

}
