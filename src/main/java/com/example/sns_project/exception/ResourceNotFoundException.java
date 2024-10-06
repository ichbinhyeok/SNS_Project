package com.example.sns_project.exception;

// 자원 미발견 예외 클래스
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)  // 404 Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // 앞으로: 추가적인 예외 상황에 대한 메서드 추가
}
