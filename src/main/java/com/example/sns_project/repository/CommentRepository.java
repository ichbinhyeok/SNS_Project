package com.example.sns_project.repository;


// 댓글 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);  // 게시글 ID로 댓글 조회
    // 특정 사용자의 댓글 조회를 위한 메서드
    List<Comment> findByUserId(Long authorId);


    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 댓글 삭제 등)
}
