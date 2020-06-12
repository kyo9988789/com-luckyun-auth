package com.luckyun.auth.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

import com.alibaba.fastjson.JSONObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 生成jwt密钥
 * @author yangj080
 *
 */
public class JwtTokenUtil {

	private static final String CLAIM_KEY_USERNAME = "sub";
	private static final String CLAIM_KEY_CREATED = "created";

	private final static String SECRET = "lucksoft";

	public final static Long EXPIRATION = (long) (60 * 60 * 24 * 3);
	
	private final static Integer PALENGTH = 2;

	public static JSONObject getUsernameAndCompanyFromToken(String token) {
		JSONObject jsonObject = new JSONObject();
		try {
			final Claims claims = getClaimsFromToken(token);
			String username = claims.getSubject();
			String[] uSplit = username.split("-_-");
			if(uSplit.length == PALENGTH) {
				jsonObject.put("username", uSplit[0]);
				jsonObject.put("companyid", uSplit[1]);
			}else {
				jsonObject.put("username", username);
			}
		} catch (Exception e) {
			jsonObject.put("username", null);
		}
		return jsonObject;
	}
	
	public static String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	public Date getCreatedDateFromToken(String token) {
		Date created;
		try {
			final Claims claims = getClaimsFromToken(token);
			created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
		} catch (Exception e) {
			created = null;
		}
		return created;
	}

	public static Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = getClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	private static Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	private static Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + EXPIRATION * 1000);
	}

	public static Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		if(expiration == null) {
			return true;
		}
		return expiration.before(new Date());
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	public static String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>(16);
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		claims.put(CLAIM_KEY_CREATED, new Date());
		return generateToken(claims);
	}

	public static String generateToken(UserDetails userDetails,Long icompanyid) {
		Map<String, Object> claims = new HashMap<>(16);
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername()+"-_-"+icompanyid);
		claims.put(CLAIM_KEY_CREATED, new Date());
		return generateToken(claims);
	}
	
	public static String generateToken(Map<String, Object> claims) {
		return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes()).compact();
	}

	public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
		final Date created = getCreatedDateFromToken(token);
		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !isTokenExpired(token);
	}

	public String refreshToken(String token) {
		String refreshedToken;
		try {
			final Claims claims = getClaimsFromToken(token);
			claims.put(CLAIM_KEY_CREATED, new Date());
			refreshedToken = generateToken(claims);
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

}
