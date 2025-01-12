package com.example.sns_project.repository;


// 댓글 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);  // 게시글 ID로 댓글 조회
    // 특정 사용자의 댓글 조회를 위한 메서드
    List<Comment> findByUserId(Long authorId);

    // 페이징을 위한 댓글 ID 조회
    @Query("SELECT c.id FROM Comment c")
    List<Long> findCommentIdsByPage(Pageable pageable);

    // N+1 문제 방지를 위한 페치 조인 쿼리
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.likes WHERE c.id IN :ids")
    List<Comment> findAllWithLikesByIds(@Param("ids") List<Long> ids);


    // 최상위 댓글 페이징 조회
    Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);

    // N+1 문제 해결을 위한 fetch join
    @Query("SELECT DISTINCT c FROM Comment c " +
            "LEFT JOIN FETCH c.childrenComments " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.createdDate")
    List<Comment> findAllWithChildrenByPostId(@Param("postId") Long postId);


    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.parentComment.id = :parentId")
    Page<Comment> findByParentCommentId(@Param("parentId") Long parentId, Pageable pageable);

    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 댓글 삭제 등)
}
