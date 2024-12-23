package com.example.sns_project.jwt;

import com.example.sns_project.exception.UnauthorizedException;
import com.example.sns_project.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 로그인 요청은 인증 제외
        if (request.getRequestURI().equals("/api/auth/login")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        // 헤더가 없거나 Bearer로 시작하지 않는 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("인증 토큰이 필요합니다.");
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                throw new UnauthorizedException("유효하지 않은 토큰입니다.");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            return true;

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("토큰이 만료되었습니다.");
        } catch (Exception e) {
            throw new UnauthorizedException("토큰 처리 중 오류가 발생했습니다.");
        }
    }
}