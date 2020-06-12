package com.luckyun;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
public class AuthApplication extends MainApplication{
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}
}