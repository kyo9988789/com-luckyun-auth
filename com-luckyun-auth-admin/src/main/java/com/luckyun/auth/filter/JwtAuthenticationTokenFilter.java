package com.luckyun.auth.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSONObject;
import com.luckyun.auth.config.AuthBaseConfig;
import com.luckyun.auth.helper.LoginRedisHelper;
import com.luckyun.auth.util.JwtTokenUtil;
/**
 * 登录拦截器
 * @author yangj080
 *
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private AuthBaseConfig authBaseConfig;
	
	private static final String devMode = "debug";
	
	private static final String BEARER = "Bearer ";
	
	@Autowired
	private LoginRedisHelper loginRedisHelper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith(BEARER) 
				&& request.getRequestURI().indexOf("/loginin") < 0) {
			final String authToken = authHeader.substring("Bearer ".length());
			String username = JwtTokenUtil.getUsernameFromToken(authToken);
			//if(tokenIsExist(username, authToken) || devMode.equals(authBaseConfig.getDevMode())) {
			if(tokenIsExist(username, authToken)) {
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
				chain.doFilter(request, response);
			}else {
				//密钥已被手动移除
				JSONObject result = new JSONObject();
				result.put("code", "40001");
				result.put("msg", "密钥已被移除");
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
		}else {
			chain.doFilter(request, response);
		}
	}
	/**
	 * 确定当前token是否存在
	 * @param username 当前用户名称,对应key
	 * @param token 当前token值
	 * @return
	 */
	private boolean tokenIsExist(String username,String token) {
		String stringLists =  loginRedisHelper.getCurrentUserToken(username);
		if(stringLists != null && !"".equals(stringLists)) {
			if(stringLists.equals(token)) {
				return true;
			}
		}
		return false;
	}
}
