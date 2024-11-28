package com.example.sns_project.repository;

// 사용자 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);  // 이메일로 사용자 조회
    Optional<User> findByUsername(String username); // 사용자 이름으로 조회

    // 친구가 아닌 사용자 목록 조회
    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT f.user2.id FROM Friendship f WHERE f.user1.id = :userId) AND u.id <> :userId")
    List<User> findNonFriendsByUserId(@Param("userId") Long userId);
    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 사용자 삭제 등)

    // 특정 사용자명이 존재하는지 확인하는 메서드
    boolean existsByUsername(String username);

}

