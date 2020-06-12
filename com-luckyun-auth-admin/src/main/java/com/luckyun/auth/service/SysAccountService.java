package com.luckyun.auth.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.luckyun.auth.mapper.SysAccountMapper;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.core.data.BaseService;
import com.luckyun.core.tool.Md5HelperUtils;
import com.luckyun.model.user.SysAccount;

@Service
public class SysAccountService extends BaseService<SysAccount>{

	@Autowired
	private SysAccountMapper sysAccountMapper;
	
	@Autowired
	private SysUserInfoService sysUserInfoService;
	
	public void addSysUserInfo(SysAccount entity) {
		addUser(entity);
	}
	
	private void addUser(SysAccount entity) {
		SysAccount isExists = sysAccountMapper.findBySloginid(entity.getSloginid());
		if(isExists == null) {
			if(entity.getIstate() == null) {
				entity.setIstate(1);//账号默认启用
			}
			if(entity.getDovertime() == null) {
				//过期时间为空即永不过期
				entity.setDovertime(new Date(4102358400000L));
			}
			if(!StringUtils.isEmpty(entity.getSpassword())) {
				entity.setSpassword(Md5HelperUtils.md5(entity.getSpassword()));
			}
			super.insert(entity);
			//添加用户详情
			sysUserInfoService.addSysUserInfo(entity);
		}
	}
	
	@Override
	public BaseMapper<SysAccount> getMapper() {
		return sysAccountMapper;
	}

}
