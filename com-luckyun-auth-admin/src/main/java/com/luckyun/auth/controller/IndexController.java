package com.luckyun.auth.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luckyun.auth.config.AuthBaseConfig;
import com.luckyun.auth.entity.AuthSysUser;
import com.luckyun.auth.entity.SysUserLogin;
import com.luckyun.auth.entity.common.LoginEntity;
import com.luckyun.auth.service.AuthSysModuleService;
import com.luckyun.auth.service.AuthSysUserService;
import com.luckyun.auth.util.IpUtils;
import com.luckyun.core.response.ApiResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 授权控制器
 * 
 * 2019年05月22日,上午15:18
 * {@link com.lucksoft.auth.controller.IndexController}
 * @author yangj080
 * @version 1.0.0
 *
 */
@RestController
@Slf4j
public class IndexController {
	
	@Autowired
	private AuthSysUserService sysUserService;
	
	@Autowired
	private AuthSysModuleService sysMenuService;

	@Autowired
	private AuthBaseConfig authBaseConfig;
	
	private static final String DEVMODE = "debug";
	
	/**
	 * 登录
	 * @param loginEntity 登录信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/loginin")
	public ApiResult loginin(@RequestBody LoginEntity loginEntity,HttpServletRequest request) throws Exception{
		ApiResult apiResult = new ApiResult();
		//记录登录日志 start
		SysUserLogin sysUserLogin = new SysUserLogin();
		sysUserLogin.setSloginid(loginEntity.getUsername());
		sysUserLogin.setSloginip(IpUtils.getIP(request));
		sysUserLogin.setSloginmac(null);
		if(!StringUtils.isEmpty(loginEntity.getUsername()) 
				&& !StringUtils.isEmpty(loginEntity.getPassword())) {
			apiResult = this.sysUserService.login(loginEntity,sysUserLogin);
		}else {
			throw new Exception("账号密码不能为空");
		}
		return apiResult;
	}
	
	@RequestMapping("/feign/single/loginin/noGetway")
	public ApiResult singleLoginin(@RequestBody LoginEntity loginEntity,HttpServletRequest request) {
		Long startTime = System.currentTimeMillis();
		//记录日志
		SysUserLogin sysUserLogin = new SysUserLogin();
		sysUserLogin.setSloginid(loginEntity.getUsername());
		sysUserLogin.setSloginip(IpUtils.getIP(request));
		sysUserLogin.setSloginmac(null);
		ApiResult apiResult = sysUserService.getSingleLogin(loginEntity,sysUserLogin);
		Long endTime = System.currentTimeMillis();
		if(endTime-startTime < 1000) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error("延时生成动画失败");
			}
		}
		return apiResult;
	}
	
	/**
	 * 
	 * <p>Title: refreshToken</p>  
	 * <p>Description: token刷新</p>  
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/do/refresh/token/noAuth")
	public ApiResult refreshToken(HttpServletRequest request){
		return this.sysUserService.doRefreshToken();
	}
	
	@RequestMapping("/test")
	public String testUrl() {
		return "test";
	}
	
	@RequestMapping("/auth")
	public AuthSysUser auth(@RequestParam("auth") String authUrl){
		Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		AuthSysUser authSysUser = new AuthSysUser();
		if (object instanceof AuthSysUser) {
			authSysUser = (AuthSysUser) object;
		}
		String operateResult = "",moduleUrl = "",sysUrl="";
		if(authUrl != "" ) {
			//字符串中符合条件的数据
			int sysSub = this.charIndex(authUrl, 1, '/');
			int mstartSub = this.charIndex(authUrl, 2, '/');
			int strSub = this.charIndex(authUrl, lastIndex(authUrl,'/'), '/');
			sysUrl = authUrl.substring(sysSub+1,mstartSub).toLowerCase();
			moduleUrl = authUrl.substring(mstartSub+1, strSub).toLowerCase();
			operateResult = authUrl.substring(strSub + 1).toLowerCase();
		}
		//是否属于放行地址
		boolean flag = false;
		if(DEVMODE.equals(authBaseConfig.getDevMode())
				|| authSysUser.getSloginid().toLowerCase().equals(authBaseConfig.getReleaseUser().toLowerCase())) {
			//将当前url拥有的操作列表添加数据中
			String operateLists = sysUserService.getOperates(authSysUser.getIndocno()
					, sysUrl+"/"+moduleUrl);
			authSysUser.setOperateLists(operateLists);
			flag = true;
		}
		if(!flag) {
			//是否拥有权限
			List<String> operates = new ArrayList<>();
			flag = this.sysMenuService.findModuleByUser(
					authSysUser.getIndocno(),sysUrl+"/"+moduleUrl, operateResult,operates);
			//将当前url拥有的操作列表添加数据中
			String operateLists = String.join(",", operates);
			authSysUser.setOperateLists(operateLists);
		}
		return authSysUser;
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
