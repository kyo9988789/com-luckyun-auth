package com.luckyun.auth.provider.feign.intercepter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class AuthFeignInterceptor implements RequestInterceptor{
	
	@Override
	public void apply(RequestTemplate template) {
		template.header("rpc", "true");
		try {
			HttpServletRequest httpServletRequest = 
					((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			String auth = (String) httpServletRequest.getAttribute("Authorization");
			if(StringUtils.isEmpty(auth)) {
				auth = httpServletRequest.getParameter("token");
				if(!StringUtils.isEmpty(auth)) {
					auth = "Bearer " + auth;
				}
			}
			if(!StringUtils.isEmpty(auth)) {
				template.header("Authorization", auth);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}