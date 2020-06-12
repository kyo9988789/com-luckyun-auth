package com.luckyun.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.luckyun.auth.entity.AuthSysUser;
import com.luckyun.auth.mapper.AuthSysUserMapper;
/**
 * 重写security登录服务
 * 2019年01月15日,下午9:51
 * {@link com.luckyun.auth.service.lucksoft.auth.service.impl.CustomUserDetailServiceImpl}
 * @author yangj080
 *
 */
@Service
public class CustomUserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private AuthSysUserMapper sysUserMapper;
	
	@Override
	public UserDetails loadUserByUsername(String sloginid) 
			throws UsernameNotFoundException {
		AuthSysUser sysUser = new AuthSysUser();
		sysUser = getCloginUser(sloginid);
		if(sysUser == null) {
			throw new UsernameNotFoundException("用户不存在");
		}
		return sysUser;
	}
	
	private AuthSysUser getCloginUser(String sloginid) {
		AuthSysUser sysUser = sysUserMapper.findAsBySloginid(sloginid);
		//账号超期
		if(sysUser.getDovertime() != null) {
			LocalDateTime dovertime = sysUser.getDovertime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			if(dovertime.isBefore(LocalDateTime.now())) {
				sysUser.setIstate(3);
			}
		}
		//移除用户登录时获取的所属角色与管辖部门
//		if(sysUser !=null) {
//			List<SysDept> depts = sysUserMapper.findDeptByUser(sysUser.getIndocno());
//			List<SysRole> roles = sysUserMapper.findRoleByUser(sysUser.getIndocno());
//			sysUser.setDeptList(depts);
//			sysUser.setRoleList(roles);
//		}
		return sysUser;
	}
}
