package com.luckyun.auth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.luckyun.core.data.BaseMapper;
import com.luckyun.model.operate.SysOperate;

@Repository
public interface AuthSysOperateMapper extends BaseMapper<SysOperate>{

	/**
	 * 获取当前用户在当前访问地址下面的操作
	 * @param iuserid 用户编号
	 * @param moduleUrl 模块地址
	 * @return 操作列表
	 */
	List<SysOperate> findBySysModule(@Param("iuserid") Long iuserid
			,@Param("moduleUrl") String moduleUrl);
}
