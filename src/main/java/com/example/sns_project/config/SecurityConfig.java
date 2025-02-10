package com.example.sns_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // 모든 요청 허용
                );

        return http.build();
    }


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .csrf().disable()
//                .authorizeRequests()
//                .requestMatchers("/api/auth/**").permitAll()
//                .anyRequest().authenticated();
//
//        return http.build();
//    }
//}


    // 앞으로: JWT 필터 등록 및 CORS 정책 설정 추가 필요
}

//
//
//package com.example.sns_project.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()  // 인증 관련 API는 모든 사용자 접근 허용
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger UI 접근 허용
//                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
//                );
//
//        return http.build();
//    }
//
//    // 앞으로: JWT 필터 등록 및 CORS 정책 설정 추가 필요
//}
