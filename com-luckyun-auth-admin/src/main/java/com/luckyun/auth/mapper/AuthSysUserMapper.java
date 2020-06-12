package com.luckyun.auth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.luckyun.auth.entity.AuthSysUser;
import com.luckyun.core.data.BaseMapper;
import com.luckyun.model.dept.SysDept;
import com.luckyun.model.module.SysModule;
import com.luckyun.model.role.SysRole;
import com.luckyun.model.user.SysAccount;

@Repository
public interface AuthSysUserMapper extends BaseMapper<SysAccount>{

	
	/**
	 * 根据登录名称获取用户对象
	 * @param sloginid 登录名称
	 * @return 登录用户
	 */
	SysAccount findBySloginid(@Param("sloginid") String sloginid);
	
	/**
	 * 根据登录名称获取用户的登录信息
	 * @param sloginid 登录名称
	 * @return 授权的登录用户
	 */
	AuthSysUser findAsBySloginid(@Param("sloginid") String sloginid);
	
	/**
	 * 根据账号密码获取登录用户信息
	 * @param sloginid 账号
	 * @param spassword 密码
	 * @return 返回用户
	 */
	AuthSysUser findBySloginidAndSpassword(@Param("sloginid") String sloginid
			,@Param("spassword") String spassword);
	
	/**
	 * 根据人员获取管辖部门列表
	 * @param uid 人员编号
	 * @return 人员列表
	 */
	List<SysDept> findDeptByUser(@Param("uid") Long uid);
	
	/**
	 * 根据人员获取角色列表
	 * @param uid 人员编号
	 * @return 角色列表
	 */
	List<SysRole> findRoleByUser(@Param("uid") Long uid);
	
	/**
	 * 根据地址获取对应的模块地址
	 * @return 请求地址访问的模块地址
	 */
	List<SysModule> findModuleByPathalias(@Param("spathalias") String spathalias);
}
