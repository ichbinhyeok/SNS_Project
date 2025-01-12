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
public class Comment extends BaseEntity {
    // ID 필드는 BaseEntity에서 상속받음

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;            // 게시글

    @Column(nullable = false)
    private String content;       // 댓글 내용

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 작성자 ID를 참조
    private User user;// 작성자


    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> childrenComments = new HashSet<>(); // 대댓글 목록

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; // 부모 댓글

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>(); // 댓글의 좋아요 목록

    @Column(nullable = false)
    private int depth = 0;  // 기본값 0으로 설정

    // 편의 메서드 추가
    public void addChildComment(Comment child) {
        childrenComments.add(child);
        child.setParentComment(this);
        child.setDepth(this.depth + 1);
    }

    // 전체 하위 댓글 수를 계산하는 메서드 추가
    public int calculateTotalReplies() {
        int total = 0;
        for (Comment child : childrenComments) {
            // 직계 자식 댓글 + 각 자식의 하위 댓글 수
            total += 1 + child.calculateTotalReplies();
        }
        return total;
    }
}
