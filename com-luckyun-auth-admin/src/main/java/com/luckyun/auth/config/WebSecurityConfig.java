package com.luckyun.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.luckyun.auth.common.Md5PasswordEncoder;
import com.luckyun.auth.filter.JwtAuthenticationTokenFilter;
import com.luckyun.auth.helper.EntryPointUnauthorizedHandler;
import com.luckyun.auth.helper.RestAccessDeniedHandler;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 
 * @author yj
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConfigurationProperties("security")
@Data
@EqualsAndHashCode(callSuper=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService customUserDetailsService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new Md5PasswordEncoder();
	}

	@Autowired
	private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

	@Autowired
	private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;

	@Autowired
	private RestAccessDeniedHandler restAccessDeniedHandler;

	@Autowired
	@Qualifier("loginSuccessHandler")
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	private List<String> nologin;
	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
			.antMatchers("/auth/loginin","/feign/**","/restart/index","/druid/**").permitAll()
			.antMatchers(nologin.toArray(new String[nologin.size()])).permitAll()
			.anyRequest().authenticated()
				.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
					.formLogin().loginPage("/login").successHandler(authenticationSuccessHandler).permitAll()
				.and()
					.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
		http.exceptionHandling().authenticationEntryPoint(entryPointUnauthorizedHandler)
				.accessDeniedHandler(restAccessDeniedHandler);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// super.configure(web);
		web.ignoring().antMatchers("/static/**", "/css/**");
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
