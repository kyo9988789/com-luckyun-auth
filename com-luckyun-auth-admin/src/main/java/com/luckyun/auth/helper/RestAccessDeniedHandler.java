package com.luckyun.auth.helper;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

/**
 * 403的拒绝访问返回
 * 2019年01月15日,下午9:46
 * {@link com.luckyun.auth.helper.RestAccessDeniedHandler}
 * @author yangj080
 *
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest arg0, HttpServletResponse response, AccessDeniedException arg2)
			throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		// response.setStatus(403);
		JSONObject result = new JSONObject();
		result.put("code", 403);
		result.put("msg", "拒绝访问");
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
