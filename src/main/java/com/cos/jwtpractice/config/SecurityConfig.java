package com.cos.jwtpractice.config;

import com.cos.jwtpractice.config.jwt.JwtAuthenticationFilter;
import com.cos.jwtpractice.filter.MyFilter1;
import com.cos.jwtpractice.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final CorsFilter corsFilter;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = sharedObject.build();

        http.authenticationManager(authenticationManager);
        http
                // deprecated 수정해야 함.
                //.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class) // 기존 필터에 들어가기 전에 커스텀 필터를 걸자.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 안씀
                .addFilter(corsConfig.corsFilter()) // cross origin 요청이 와도 상관없음
                .addFilter(new JwtAuthenticationFilter(authenticationManager)) // AuthenticationManager를 넘겨야 함
                .formLogin(form -> form.disable()) // form 태그로 로그인을 안함
                .httpBasic(basic -> basic.disable()) // 기본 http 방식 로그인 안씀
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("api/v1/user/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                        .requestMatchers("api/v1/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll()
                );

    return http.build();
    }
}
