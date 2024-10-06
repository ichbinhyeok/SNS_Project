package com.example.sns_project.model;

// 사용자 역할 정보를 저장하는 엔티티 클래스
// 데이터베이스와 매핑됩니다.
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 역할 ID

    @Column(nullable = false, unique = true)
    private String name;          // 역할 이름 (예: USER, ADMIN)

    // Getter 및 Setter 메서드

    // 앞으로: JPA 어노테이션 추가 및 관계 설정
}
