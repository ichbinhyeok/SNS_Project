package com.example.sns_project.model;

// 댓글 정보를 저장하는 엔티티 클래스
// 데이터베이스와 매핑됩니다.
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 댓글 ID

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;            // 게시글

    @Column(nullable = false)
    private String content;       // 댓글 내용

    @Column(name = "author_id", nullable = false)
    private Long authorId;        // 작성자 ID

    @OneToMany(mappedBy = "parentComment",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Comment> childrenComments = new HashSet<>(); //대댓글 목록

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; //부모 댓글


    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>(); // 댓글의 좋아요 목록

    // Getter 및 Setter 메서드

    // 앞으로: JPA 어노테이션 추가 및 관계 설정
}
