package com.luckyun.auth.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.luckyun.auth.config.AuthBaseConfig;
import com.luckyun.auth.entity.AuthSysUser;
import com.luckyun.auth.helper.LoginRedisHelper;
import com.luckyun.auth.mapper.AuthSysUserMapper;
import com.luckyun.auth.service.AuthSysModuleService;
import com.luckyun.auth.service.AuthSysUserService;
import com.luckyun.model.dept.SysDept;
import com.luckyun.model.module.SysModule;
import com.luckyun.model.role.SysRole;

@RestController
@RequestMapping("/feign")
public class FeignAuthController {
	
	@Autowired
	private AuthSysUserService sysUserService;
	
	@Autowired
	private AuthSysModuleService sysMenuService;
	
	@Autowired
	private AuthSysUserMapper sysUserMapper;

	@Autowired
	private AuthBaseConfig authBaseConfig;
	
	private static final String DEVMODE = "debug";
	
	@Autowired
	private LoginRedisHelper loginRedisHelper;
	
	@GetMapping("/check/auth")
	public JSONObject checkUrlAuth(@RequestParam("authUrl") String authUrl,
			@RequestParam(required = false) String isOld) {
		JSONObject result = new JSONObject();
		try {
			Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			AuthSysUser authSysUser = new AuthSysUser();
			if (object instanceof AuthSysUser) {
				authSysUser = (AuthSysUser) object;
				Long icompanyId = loginRedisHelper.getLoginIcompanyId(authSysUser.getSloginid());
				authSysUser.setIcompanyid(icompanyId);
			}else {
				result.put("code", 0);
				result.put("content", "用户未登录");
				return result;
			}
			//老服务会提供管辖部门与角色,新服务并不会提供角色和管辖部门
			if(!StringUtils.isEmpty(isOld) && Boolean.parseBoolean(isOld)) {
				setAuthDeptAndRoles(authSysUser);
			}
			String operateResult = "",moduleUrl = "",sysUrl="";
			if(authUrl != "" ) {
				//字符串中符合条件的数据
				int sysSub = this.charIndex(authUrl, 1, '/');
				int mstartSub = this.charIndex(authUrl, 2, '/');
				int strSub = this.charIndex(authUrl, lastIndex(authUrl,'/'), '/');
				sysUrl = authUrl.substring(sysSub+1,mstartSub);
				moduleUrl = authUrl.substring(mstartSub+1, strSub);
				operateResult = authUrl.substring(strSub + 1).toLowerCase();
			}
			//是否属于放行地址
			boolean flag = false;
			String authAccountUrl = "/" + sysUrl+"/"+moduleUrl + "/";
			
			boolean noInterceptor = DEVMODE.equals(authBaseConfig.getDevMode()) || 
					isNoInterceptor(authBaseConfig.getNoInterceptorContainer(),authUrl,operateResult,"container")
					|| isNoInterceptor(authBaseConfig.getNoInterceptorStartwith(),authUrl,operateResult,"startwith")
					|| isNoInterceptor(authBaseConfig.getNoInterceptorEndwith(),authUrl,operateResult,"endwith")
					|| authSysUser.getSloginid().toLowerCase().equals(authBaseConfig.getReleaseUser().toLowerCase());
			if(noInterceptor) {
				//将当前url拥有的操作列表添加数据中
				String operateLists = sysUserService.getOperates(authSysUser.getIndocno()
						, authAccountUrl);
				authSysUser.setOperateLists(operateLists);
				flag = true;
			}
			if(!flag) {
				//是否拥有权限
				List<String> operates = new ArrayList<>();
				flag = this.sysMenuService.findModuleByUser(
						authSysUser.getIndocno(),authAccountUrl, operateResult,operates);
				//将当前url拥有的操作列表添加数据中
				String operateLists = String.join(",", operates);
				authSysUser.setOperateLists(operateLists);
			}
			if(flag) {
				List<SysModule> sysModules = sysUserMapper.findModuleByPathalias(authAccountUrl);
				if(sysModules != null && sysModules.size() >= 1) {
					authSysUser.setCmodulenm(sysModules.get(0).getSname());
				}
				result.put("code", 1);
				result.put("content", authSysUser);
			}else {
				result.put("code", 0);
				result.put("content", "no auth");
			}
		}catch(Exception e) {
			e.printStackTrace();
			result.put("code", 0);
			result.put("content", e.getMessage());
		}
		return result;
	}
	
	private void setAuthDeptAndRoles(AuthSysUser authSysUser) {
		if(authSysUser != null) {
			List<SysDept> depts = sysUserMapper.findDeptByUser(authSysUser.getIndocno());
			List<SysRole> roles = sysUserMapper.findRoleByUser(authSysUser.getIndocno());
			authSysUser.setDeptList(depts);
			authSysUser.setRoleList(roles);
		}
	}
	
	private boolean isNoInterceptor(String splitNoInterceptor,String path,String operate,
			String stype) {
		if(!StringUtils.isEmpty(splitNoInterceptor)) {
			String[] splitStr = splitNoInterceptor.split(",");
			for(String sp : splitStr) {
				if("container".equals(stype)) {
					if(path.toLowerCase().contains(sp.toLowerCase())) {
						return true;
					}
				}
				if("startwith".equals(stype)) {
					if(operate.toLowerCase().startsWith(sp.toLowerCase())) {
						return true;
					}
				}
				if("endwith".equals(stype)) {
					if(operate.toLowerCase().endsWith(sp.toLowerCase())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private Integer charIndex(String str,Integer record,char cr) {
		int i = 0;
		for(int j = 0;j<str.length();j++) {
			char c = str.charAt(j);
			if(c == cr) {
				i++;
			}
			if(i == record) {
				return j;
			}
		}
		return 0;
	}
	
	private Integer lastIndex(String str,char cr) {
		int i = 0;boolean flag = false;
		for(int j = 0;j<str.length();j++) {
			char c = str.charAt(j);
			if(c == cr) {
				i++;
				flag = true;
				continue;
			}
			flag = false;
		}
		if(flag) {
			return i - 1;
		}
		return i;
	}
}
