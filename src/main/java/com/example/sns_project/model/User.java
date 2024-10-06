package com.example.sns_project.model;

// 사용자 정보를 저장하는 엔티티 클래스
// 데이터베이스와 매핑됩니다.
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 사용자 ID

    @Column(nullable = false, unique = true)
    private String username;      // 사용자 이름

    @Column(nullable = false, unique = true)
    private String email;         // 사용자 이메일

    @Column(nullable = false)
    private String password;      // 사용자 비밀번호

    // Getter 및 Setter 메서드

    // 앞으로: JPA 어노테이션 추가 및 관계 설정
}
