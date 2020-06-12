package com.luckyun.auth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.luckyun.auth.config.AuthBaseConfig;
import com.luckyun.auth.entity.AuthSysUser;
import com.luckyun.auth.entity.AuthTokenDetail;
import com.luckyun.auth.entity.SysUserLogin;
import com.luckyun.auth.entity.common.LoginEntity;
import com.luckyun.auth.helper.LoginRedisHelper;
import com.luckyun.auth.mapper.AuthSysOperateMapper;
import com.luckyun.auth.mapper.AuthSysUserLoginMapper;
import com.luckyun.auth.mapper.AuthSysUserMapper;
import com.luckyun.auth.mapper.SysCompanyMapper;
import com.luckyun.auth.util.JwtTokenUtil;
import com.luckyun.core.exception.CustomException;
import com.luckyun.core.km.config.LuckCoreLuttucePools;
import com.luckyun.core.response.ApiResult;
import com.luckyun.core.tool.Md5HelperUtils;
import com.luckyun.model.company.SysCompany;
import com.luckyun.model.operate.SysOperate;

/**
 * 
 * @author yangj080
 *
 */
@Service
public class AuthSysUserService {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private AuthSysUserLoginMapper sysUserLoginMapper;
	
	@Autowired
	private AuthSysOperateMapper sysOperateMapper;
	
	@Autowired
	private AuthBaseConfig authBaseConfig;
	
	@Autowired
	private LoginRedisHelper loginRedisHelper;
	
	@Autowired
	private AuthSysUserMapper authSysUserMapper;
	
	/**
	 * 登录验证
	 */
	public ApiResult login(LoginEntity loginEntity,SysUserLogin sysUserLogin){
		ApiResult apiResult = new ApiResult();
		try {
			UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(loginEntity.getUsername(), loginEntity.getPassword());
			final Authentication authentication = authenticationManager.authenticate(upToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// Reload password post-security so we can generate token
			Object object = authentication.getPrincipal();
			
			AuthSysUser authSysUser = new AuthSysUser();
			if (object instanceof AuthSysUser) {
				authSysUser = (AuthSysUser) object;
			}
			String token = "";
			if(authBaseConfig.isSingleLogin()) {
				token = JwtTokenUtil.generateToken(authSysUser);
				//存入缓存
				loginRedisHelper.setCurrentUserToken(loginEntity.getUsername(), token);
			}else {
				token = loginRedisHelper.getCurrentUserToken(loginEntity.getUsername());
				if(StringUtils.isEmpty(token) || JwtTokenUtil.isTokenExpired(token)) {
					token = JwtTokenUtil.generateToken(authSysUser);
					//存入缓存
					loginRedisHelper.setCurrentUserToken(loginEntity.getUsername(), token);
				}
			}
			//保存登录日志
			sysUserLogin.setIuserid(authSysUser.getIndocno());
			sysUserLogin.setSusernm(authSysUser.getSname());
			sysUserLogin.setIlogintype(1);
			sysUserLogin.setSloginmsg("登录成功");
			Long curIndocno = LuckCoreLuttucePools.getHincrById(sysUserLogin.__getTableName());
			sysUserLogin.setIndocno(curIndocno);
			sysUserLoginMapper.insert(sysUserLogin);
			//设置authToken详情
			apiResult.setData(token);
			AuthTokenDetail authTokenDetail = new AuthTokenDetail();
			authTokenDetail.setExpire(loginRedisHelper.getExpireKey(loginEntity.getUsername()));
			apiResult.setMainInfo(authTokenDetail);
			//当前用户拥有公司,并且没有以任何公司的身份登录
			if(authSysUser.getIcompanyid() != null && loginEntity.getIcompanyid() == null) {
				loginEntity.setIcompanyid(authSysUser.getIcompanyid());
			}
			//当前账号登录的公司写入缓存
			setCacheCompanyId(loginEntity);
			return apiResult;
		}catch(Exception e) {
			//记录登录失败的日志
			sysUserLogin.setIlogintype(0);
			sysUserLogin.setSloginmsg("登录失败,原因:"+ e.getMessage());
			Long curIndocno = LuckCoreLuttucePools.getHincrById(sysUserLogin.__getTableName());
			sysUserLogin.setIndocno(curIndocno);
			sysUserLoginMapper.insert(sysUserLogin);
			e.printStackTrace();
			throw new CustomException("当前账号密码错误");
		}
		
	}
	
	public ApiResult getSingleLogin(LoginEntity loginEntity,SysUserLogin sysUserLogin) {
		ApiResult apiResult = new ApiResult();
		AuthSysUser authSysUser = authSysUserMapper
				.findBySloginidAndSpassword(loginEntity.getUsername(), loginEntity.getPassword());
		if(authSysUser == null) {
			String password = Md5HelperUtils.md5(loginEntity.getPassword());
			authSysUser = authSysUserMapper
					.findBySloginidAndSpassword(loginEntity.getUsername(), password);
		}
		Long curIndocno = LuckCoreLuttucePools.getHincrById(sysUserLogin.__getTableName());
		sysUserLogin.setIndocno(curIndocno);
		if(authSysUser != null) {
			//保存登录日志
			sysUserLogin.setIuserid(authSysUser.getIndocno());
			sysUserLogin.setSusernm(authSysUser.getSname());
			sysUserLogin.setIlogintype(1);
			sysUserLogin.setSloginmsg("单点登录成功");
			sysUserLoginMapper.insert(sysUserLogin);
			
			String token = getToken(authSysUser);
			apiResult.setData(token);
			AuthTokenDetail authTokenDetail = new AuthTokenDetail();
			authTokenDetail.setExpire(loginRedisHelper.getExpireKey(loginEntity.getUsername()));
			apiResult.setMainInfo(authTokenDetail);
			return apiResult;
		}else {
			//记录登录失败的日志
			sysUserLogin.setIlogintype(0);
			sysUserLogin.setSloginmsg("单点登录失败,原因:用户不存在");
			sysUserLoginMapper.insert(sysUserLogin);
			
			throw new CustomException("授权失败");
		}
	}
	
	private String getToken(AuthSysUser authSysUser) {
		String token = "";
		if(authBaseConfig.isSingleLogin()) {
			token = JwtTokenUtil.generateToken(authSysUser);
			//存入缓存
			loginRedisHelper.setCurrentUserToken(authSysUser.getSloginid(), token);
		}else {
			token = loginRedisHelper.getCurrentUserToken(authSysUser.getSloginid());
			if(StringUtils.isEmpty(token) || JwtTokenUtil.isTokenExpired(token)) {
				token = JwtTokenUtil.generateToken(authSysUser);
				//存入缓存
				loginRedisHelper.setCurrentUserToken(authSysUser.getSloginid(), token);
			}
		}
		return token;
	}

	public String getOperates(Long iuserid, String moduleUrl) {
		try {
			String operateList = "";
			if(StringUtils.isEmpty(operateList)) {
				List<SysOperate> sysOperates = 
						this.sysOperateMapper.findBySysModule(iuserid,moduleUrl);
				//将模块添加到对应的用户模块操作列表缓存中
				List<String> userOperates = new ArrayList<String>();
				sysOperates.forEach(e -> {
					if(!StringUtils.isEmpty(e.getSpath())) {
						userOperates.add(e.getSpath());
					}else if(!StringUtils.isEmpty(e.getCspath())){
						//自定义路径
						userOperates.add(e.getCspath());
					}
				});
				String operateStr = String.join(",", userOperates);
				return operateStr;
			}
			return operateList;
		}catch (Exception e) {
			throw new CustomException("获取操作失败;"+e.getMessage());
		}
	}

	/** (non-Javadoc)  
	 * <p>Title: doRefreshToken</p>  
	 * <p>Description: token刷新</p>    
	 * @see com.AuthSysUserService.auth.service.SysUserService#doRefreshToken()  
	 */
	public ApiResult doRefreshToken() {
		ApiResult apiResult = new ApiResult();
		try {
			Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			AuthSysUser authSysUser = new AuthSysUser();
			if (object instanceof AuthSysUser) {
				authSysUser = (AuthSysUser) object;
			}
			final String token = JwtTokenUtil.generateToken(authSysUser);
			if(null != token && !StringUtils.isEmpty(token)) {
				//redis缓存token,保证3天时效
				loginRedisHelper.setCurrentUserToken(authSysUser.getSloginid(), token);
			}
			apiResult.setData(token);
			AuthTokenDetail authTokenDetail = new AuthTokenDetail();
			authTokenDetail.setExpire(loginRedisHelper.getExpireKey(authSysUser.getSloginid()));
			apiResult.setMainInfo(authTokenDetail);
			return apiResult;
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}
	
	private void setCacheCompanyId(LoginEntity loginEntity) {
		Long icompanyId = null;
		if(loginEntity.getIcompanyid() == null) {
			icompanyId = getDefaultCompany(loginEntity.getUsername()).getIndocno();
		}else {
			icompanyId = loginEntity.getIcompanyid();
		}
		loginRedisHelper.setLoginidIcompanyId(loginEntity.getUsername(), icompanyId);
	}
	
	@Autowired
	private SysCompanyMapper sysCompanyMapper;
	/**
	 * 查找默认公司,一般是根据当前登录人部门所属的公司
	 * @return 
	 */
	public SysCompany getDefaultCompany(String username) {
		SysCompany company = null;
		//超级管理员
		if(!StringUtils.isEmpty(username) && username.equals(authBaseConfig.getReleaseUser())) {
			List<SysCompany> sysCompanies = sysCompanyMapper.findCompany();
			if(sysCompanies != null && sysCompanies.size() >= 1) {
				company = sysCompanies.get(0);
			}else {
				company = new SysCompany();
			}
		}else {
			company = sysCompanyMapper.findCompanyByUsername(username);
		}
		return company;
	}
}
