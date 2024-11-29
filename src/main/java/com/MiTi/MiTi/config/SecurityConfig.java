package com.MiTi.MiTi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/login/**", "/error", "/api/token", "/callback", "/index.html",
                                "/streaming.html", "/app.js", "/playbar.js","/home", "/main/**").permitAll()
                        .requestMatchers("/resources/**", "/static/**", "/public/**", "/src/main/scripts/**", "/error", "/login/oauth2/code/spotify").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login") // 사용자 정의 로그인 페이지
                        .defaultSuccessUrl("/main") // 로그인 성공 후 이동할 페이지
                );

        return http.build();
    }
}
