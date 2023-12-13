package com.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import com.customer.impl.UserDetailsSeviceImple;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsSeviceImple();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider adoAuthenticationProvider = new DaoAuthenticationProvider();
		adoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
		adoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return  adoAuthenticationProvider;
	}
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.csrf().disable()
		.authorizeHttpRequests()
		//.requestMatchers("/User/**").hasRole("USER")
		//.requestMatchers("/Admin/**").hasRole("ADMIN")
		.requestMatchers("/**").permitAll()
		.and().formLogin()
		.loginPage("/Login")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/User/Index")
		;
		
		httpSecurity.authenticationProvider(daoAuthenticationProvider());
		
		 DefaultSecurityFilterChain securityFilterChain = httpSecurity.build();
		return securityFilterChain;
		
	}

}
