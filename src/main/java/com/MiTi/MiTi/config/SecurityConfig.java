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
                        .requestMatchers("/", "/login", "/error", "/api/token", "/callback", "/index.html",
                                "/streaming.html", "/app.js", "/playbar.js", "/home", "/main/**").permitAll()
                        .requestMatchers("/resources/**", "/static/**", "/public/**", "/src/main/scripts/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login") // 로그인 페이지
                        .defaultSuccessUrl("/main", true) // 로그인 성공 후 이동할 페이지
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // 로그아웃 후 리다이렉트
                            response.sendRedirect("/");
                        })
                        .invalidateHttpSession(true) // 세션 무효화
                        .clearAuthentication(true) // 인증 정보 제거
                        .deleteCookies("JSESSIONID", "sp_dc", "sp_key") // 삭제할 쿠키 이름
                );

        return http.build();
    }
}