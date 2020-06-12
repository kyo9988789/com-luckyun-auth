package com.luckyun.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luckyun.auth.mapper.SysOperateMapper;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.core.data.BaseService;
import com.luckyun.model.operate.SysOperate;

@Service
public class SysOperateService extends BaseService<SysOperate>{

	@Autowired
	private SysOperateMapper sysOperateMapper;
	
	@Override
	public BaseMapper<SysOperate> getMapper() {
		return sysOperateMapper;
	}

}
