package com.example.sns_project.exception;

// 인증 실패 예외 클래스
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401 Unauthorized
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    // 앞으로: 추가적인 예외 상황에 대한 메서드 추가
}
