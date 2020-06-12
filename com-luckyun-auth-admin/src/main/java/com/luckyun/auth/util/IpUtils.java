package com.luckyun.auth.util;

import javax.servlet.http.HttpServletRequest;

/**
*
* @author Jonny
*/
public class IpUtils {

	private static final String UNKNOWN = "unknown";
	private static final String COMMA = ",";
	public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //"***.***.***.***".length() = 15
        if (ip != null && ip.indexOf(COMMA) > -1) {   
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip.replace(" ", "");
    }
	
}
