package com.luckyun.auth.provider.feign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.luckyun.auth.provider.feign.fallback.AuthSysModuleServiceFallBack;

/**
 * 人员授权情况
 * @author yangj080
 *
 */
@FeignClient(name="${application.servernm.luckyun-auth:luckyun-auth}",url = "${application.servernm.luckyun-auth-url:}",fallback = AuthSysModuleServiceFallBack.class)
public interface AuthSysModuleProvider {

	@GetMapping("/feign/check/auth")
	JSONObject checkUrlAuth(@RequestParam("authUrl") String authUrl);
	
	
	@GetMapping("/feign/check/auth")
	JSONObject checkUrlAuthIsOld(@RequestParam("authUrl") String authUrl,@RequestParam("isOld") String isOld);
	
	
	@PostMapping("/feign/single/loginin/noGetway")
	JSONObject singleLogin(Map<String,Object> params);
}
