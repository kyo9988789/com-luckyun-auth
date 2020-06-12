package com.luckyun.auth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.luckyun.auth.mapper.AuthSysOperateMapper;
import com.luckyun.model.operate.SysOperate;

/**
 * 菜单是否存在判断
 * @author yangj080
 *
 */
@Service
public class AuthSysModuleService {

	@Autowired
	private AuthSysOperateMapper sysOperateMapper ;
	
	public Boolean findModuleByUser(Long iuserid
			,String moduleUrl,String operateParams,List<String> oeprateList) 
	{
		String authCache = "";
		moduleUrl = moduleUrl.endsWith("/") ? moduleUrl : moduleUrl + "/";
		String authUrl = moduleUrl + operateParams;
		if(!authCache.contains(authUrl)) {
			List<SysOperate> sysOperates = 
					this.sysOperateMapper.findBySysModule(iuserid,moduleUrl);
			List<String> oeprateLists = getAuthPath(sysOperates);
			for(String oper : oeprateLists) {
				oeprateList.add(oper);
			}
			if(sysOperates != null) {
				for(String operate : oeprateList) {
					if(operateParams.startsWith(operate)) {
						return true;
					}
				}
			}
		}else {
			return true;
		}
		return false;
	}
	/**
	 * 当前用户在当前路径下拥有的操作
	 * @param operates 拥有的操作列表
	 * @return 操作短路径
	 */
	private List<String> getAuthPath(List<SysOperate> operates){
		List<String> allOperates = new ArrayList<String>();
		for(SysOperate operate : operates) {
			if(!StringUtils.isEmpty(operate.getSpath())) {
				allOperates.add(operate.getSpath());
			}else if(!StringUtils.isEmpty(operate.getCspath())) {
				allOperates.add(operate.getCspath());
			}
		}
		return allOperates;
	}
}
