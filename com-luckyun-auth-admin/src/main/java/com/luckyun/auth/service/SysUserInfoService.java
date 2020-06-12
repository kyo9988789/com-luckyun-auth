package com.luckyun.auth.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luckyun.auth.mapper.SysUserInfoMapper;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.core.data.BaseService;
import com.luckyun.model.user.SysAccount;
import com.luckyun.model.user.SysUser;

@Service
public class SysUserInfoService extends BaseService<SysUser>{

	@Autowired
	private SysUserInfoMapper sysUserInfoMapper;
	
	public void addSysUserInfo(SysAccount entity) {
		SysUser sysUserInfo = entity.getSysUserInfo();
		if(sysUserInfo != null) {
			sysUserInfo.setIuserid(entity.getIndocno());
		}else {
			sysUserInfo= new SysUser();
			sysUserInfo.setIuserid(entity.getIndocno());
		}
		sysUserInfo.setDregt(new Date());
		sysUserInfo.setSregid(entity.getIndocno());
		super.insert(sysUserInfo);
	}
	
	@Override
	public BaseMapper<SysUser> getMapper() {
		return sysUserInfoMapper;
	}

}
