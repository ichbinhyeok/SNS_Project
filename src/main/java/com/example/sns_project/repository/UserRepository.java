package com.example.sns_project.repository;

// 사용자 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);  // 이메일로 사용자 조회
    Optional<User> findByUsername(String username); // 사용자 이름으로 조회

    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 사용자 삭제 등)
}
