package com.luckyun.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckyun.auth.entity.AuthSysUser;
import com.luckyun.auth.entity.UpdatePwdEntity;
import com.luckyun.auth.helper.LoginRedisHelper;
import com.luckyun.auth.mapper.AuthSysUserMapper;
import com.luckyun.auth.util.JwtTokenUtil;
import com.luckyun.auth.util.Md5Utils;
import com.luckyun.core.exception.CustomException;
import com.luckyun.model.user.SysAccount;

@RequestMapping("authBaseHepler")
@RestController
public class AuthBaseHelperController {
	
	@Autowired
	private AuthSysUserMapper authSysUserMapper;
	
	@Autowired
	private LoginRedisHelper loginRedisHelper;

	@PostMapping("noAuthUpdateUserPwd")
	public void updateUserPwd(@RequestBody UpdatePwdEntity updatePwdEntity) {
		Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(object instanceof AuthSysUser) {
			if(!StringUtils.isEmpty(updatePwdEntity.getOldPwd()) 
					&& !StringUtils.isEmpty(updatePwdEntity.getNewPwd())) {
				String oldPwd = ((AuthSysUser)object).getPassword();
				//老密码通过验证
				if(Md5Utils.md5(updatePwdEntity.getOldPwd()).equals(oldPwd)){
					String newPwd = Md5Utils.md5(updatePwdEntity.getNewPwd());
					
					SysAccount account = new SysAccount();
					account.setIndocno(((AuthSysUser)object).getIndocno());
					account.setSpassword(newPwd);
					//修改密码
					authSysUserMapper.update(account);
					String token = JwtTokenUtil.generateToken((AuthSysUser)object);
					loginRedisHelper.setCurrentUserToken(((AuthSysUser)object).getSloginid(), token);
				}else {
					//老密码未通过验证
					throw new CustomException("auth.oldpassword.error");
				}
			}else {
				throw new CustomException("auth.params.error");
			}
		}else {
			throw new CustomException("auth.params.error");
		}
	}
}
