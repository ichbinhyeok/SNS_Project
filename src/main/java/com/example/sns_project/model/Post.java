package com.example.sns_project.model;

// 게시글 정보를 저장하는 엔티티 클래스
// 데이터베이스와 매핑됩니다.
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 게시글 ID

    @Column(nullable = false)
    private String title;         // 게시글 제목

    @Column(nullable = false)
    private String content;       // 게시글 내용

    @Column(name = "author_id", nullable = false)
    private Long authorId;        // 작성자 ID

    // Getter 및 Setter 메서드

    // 앞으로: JPA 어노테이션 추가 및 관계 설정
}
