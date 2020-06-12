package com.luckyun.auth.mapper;

import org.springframework.stereotype.Repository;

import com.luckyun.core.data.BaseMapper;
import com.luckyun.model.module.SysModule;

@Repository
public interface SysModuleMapper extends BaseMapper<SysModule>{

	/**
	 * 数据库中模块数
	 * @return 列表模块数
	 */
	Long findModuleCount();
}
