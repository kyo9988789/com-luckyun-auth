package com.luckyun.auth.helper;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

/**
 * 401
 * @author yangj080
 *
 */
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.setHeader("Access-Control-Allow-Origin", "*");
		// response.setStatus(401);
		JSONObject result = new JSONObject();
		result.put("code", 401);
		result.put("msg", "未获得授权,原因:"+arg2.getMessage());
		PrintWriter writer = null;
		try {
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
