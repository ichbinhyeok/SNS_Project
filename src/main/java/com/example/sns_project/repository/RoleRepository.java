package com.example.sns_project.repository;

// 역할 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);  // 역할 이름으로 조회

    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 역할 삭제 등)
}
