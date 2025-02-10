package com.example.sns_project.repository;

// 사용자 데이터 접근을 위한 JPA 레포지토리

import aj.org.objectweb.asm.commons.Remapper;
import com.example.sns_project.dto.LoginUserDTO;
import com.example.sns_project.dto.UserDetailsDTO;
import com.example.sns_project.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Query("SELECT new com.example.sns_project.dto.LoginUserDTO(u.id, u.username, u.password) " +
            "FROM User u WHERE u.username = :username")
    Optional<LoginUserDTO> findUserForLogin(@Param("username") String username);


    @Modifying
    @Query(value = "UPDATE users SET password = :newPassword WHERE id = :userId",
            nativeQuery = true)  // 네이티브 쿼리 사용
    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);




    // 특정 사용자명이 존재하는지 확인하는 메서드
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN Friendship f1 ON f1.user1 = u OR f1.user2 = u
            LEFT JOIN Friendship f2 ON 
                (f2.user1.id = :userId AND (f2.user2 = f1.user1 OR f2.user2 = f1.user2))
                OR 
                (f2.user2.id = :userId AND (f2.user1 = f1.user1 OR f2.user1 = f1.user2))
            LEFT JOIN u.posts p
            LEFT JOIN p.likes pl ON pl.user.id = :userId
            LEFT JOIN p.comments c ON c.user.id = :userId
            LEFT JOIN c.likes cl ON cl.user.id = :userId
            WHERE u.id != :userId
            AND NOT EXISTS (
                SELECT 1 FROM Friendship f 
                WHERE (f.user1.id = :userId AND f.user2.id = u.id)
                OR (f.user2.id = :userId AND f.user1.id = u.id)
            )
            GROUP BY u.id
            ORDER BY (
                COUNT(DISTINCT f2.user2) * 5 +
                COUNT(DISTINCT pl) * 2 +
                COUNT(DISTINCT c) * 3 +
                COUNT(DISTINCT cl)
            ) DESC
            """)
    List<User> findRecommendedUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT new com.example.sns_project.dto.UserDetailsDTO(
            u.username, 
            u.password,
            r.name
        )
        FROM User u
        LEFT JOIN u.roles r
        WHERE u.username = :username
        """)
    Optional<UserDetailsDTO> findUserDetailsDTO(@Param("username") String username);



}



