package com.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(getUserDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // CSRF protection ko disable karein
            .authorizeRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/User/**")).hasRole("USER") // USER ke liye URLs set karein
                .requestMatchers(new AntPathRequestMatcher("/Admin/**")).hasRole("ADMIN") // ADMIN ke liye URLs set karein
                .anyRequest().permitAll() // Baki sab URLs sabko allow karein
            )
            .formLogin()
                .loginPage("/Login") // Custom login page set karein
                .loginProcessingUrl("/dologin") // Login process URL set karein
                .defaultSuccessUrl("/User/Index") // Default success URL set karein
            .and()
            .authenticationProvider(daoAuthenticationProvider()); // Authentication provider set karein

        return http.build(); // Security filter chain ko build karein
    }
}
