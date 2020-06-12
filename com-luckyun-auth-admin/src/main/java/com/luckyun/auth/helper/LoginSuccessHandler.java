package com.luckyun.auth.helper;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.luckyun.auth.entity.AuthSysUser;
import com.luckyun.auth.util.JwtTokenUtil;
/**
 * 登录成功后的结果返回
 * 2019年01月15日,下午9:46
 * {@link com.lucksoft.auth.helper.LoginSuccessHandler}
 * @author yangj080
 *
 */
@Component("loginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Object object = authentication.getPrincipal();
		JSONObject result = new JSONObject();
		if (object != null && object instanceof AuthSysUser) {
			AuthSysUser user = (AuthSysUser) object;
			String jwtKey = JwtTokenUtil.generateToken(user);
			result.put("code", 1);
			result.put("jwt", jwtKey);
		} else {
			result.put("code", 0);
			result.put("msg", "授权发生错误");
		}
		PrintWriter writer = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=utf-8");
			writer = response.getWriter();
			writer.print(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
	}

}
