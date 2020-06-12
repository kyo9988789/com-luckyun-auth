package com.luckyun.auth.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.luckyun.core.data.BaseMapper;
import com.luckyun.model.user.SysAccount;

@Repository
public interface SysAccountMapper extends BaseMapper<SysAccount>{

	/**
	 * 查找当前用户是否存在
	 * @param sloginid 账号
	 * @return 当前账号的用户
	 */
	SysAccount findBySloginid(@Param("sloginid") String sloginid);
}
