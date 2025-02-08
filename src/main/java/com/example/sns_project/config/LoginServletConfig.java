package com.example.sns_project.config;

import com.example.sns_project.controller.AuthController;
import com.example.sns_project.dto.AuthResponse;
import com.example.sns_project.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;

@Configuration
public class LoginServletConfig {

    private final ObjectMapper objectMapper;

    public LoginServletConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ServletRegistrationBean<HttpServlet> loginServlet(AuthController authController) {
        ServletRegistrationBean<HttpServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                // Request body를 읽어서 LoginRequest 객체로 변환
                StringBuilder sb = new StringBuilder();
                String line;
                try (BufferedReader reader = req.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                // JSON을 LoginRequest 객체로 변환
                LoginRequest loginRequest = objectMapper.readValue(sb.toString(), LoginRequest.class);

                // 로그인 처리
                ResponseEntity<AuthResponse> responseEntity = authController.login(loginRequest);

                // Response 설정
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), responseEntity.getBody());
            }
        });
        registration.addUrlMappings("/api/auth/login");  // URL 패턴 수정
        registration.setLoadOnStartup(1);
        return registration;
    }
}