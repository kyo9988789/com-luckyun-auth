package com.luckyun.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 
 * @author yj
 * 创建一些常用的bean文件
 *
 */
@Configuration
public class BaseBeanConfig {

	@Bean
	public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory){
		RedisTemplate<?,?> template = new StringRedisTemplate(factory);
        return template;
	}
}
