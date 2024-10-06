package com.example.sns_project.exception;

// API 예외 처리 클래스
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)  // 400 Bad Request
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

    // 앞으로: 다양한 예외 상황에 대한 추가적인 생성자 및 메서드 추가
}
