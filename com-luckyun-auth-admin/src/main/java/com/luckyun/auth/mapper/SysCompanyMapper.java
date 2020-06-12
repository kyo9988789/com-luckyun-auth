package com.luckyun.auth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.luckyun.core.data.BaseMapper;
import com.luckyun.model.company.SysCompany;

@Repository
public interface SysCompanyMapper extends BaseMapper<SysCompany>{

	/**
	 * 获取公司列表
	 * @return 公司列表
	 */
	List<SysCompany> findCompany();
	
	/**
	 * 根据账号获取公司
	 * @param sloginid 账号
	 * @return 公司列表
	 */
	SysCompany findCompanyByUsername(@Param("sloginid") String sloginid);
}
