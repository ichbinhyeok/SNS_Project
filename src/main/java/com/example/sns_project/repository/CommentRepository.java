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

    // 페이징을 위한 댓글 ID 조회
    @Query("SELECT c.id FROM Comment c")
    List<Long> findCommentIdsByPage(Pageable pageable);

    // N+1 문제 방지를 위한 페치 조인 쿼리
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.likes WHERE c.id IN :ids")
    List<Comment> findAllWithLikesByIds(@Param("ids") List<Long> ids);

    @Query("""
    SELECT DISTINCT c FROM Comment c 
    LEFT JOIN FETCH c.user 
    WHERE c.post.id = :postId 
    AND c.parentComment IS NULL 
    ORDER BY c.createdDate DESC
    """)
    Page<Comment> findRootComments(@Param("postId") Long postId, Pageable pageable);


    @Query("""
    SELECT DISTINCT c FROM Comment c 
    LEFT JOIN FETCH c.user 
    WHERE c.parentComment.id = :parentId 
    ORDER BY c.createdDate
    """)
    Page<Comment> findChildCommentsWithUser(
            @Param("parentId") Long parentId,
            Pageable pageable
    );

    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM CommentLike cl 
        WHERE cl.comment.id = :commentId 
        AND cl.user.id = :userId
    )
    """)
    boolean existsByCommentIdAndUserId(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId
    );


    @Query(value = """
        WITH RECURSIVE CommentHierarchy AS (
            -- 초기 선택: 첫 번째 레벨의 자식들
            SELECT c.*, 1 as depth
            FROM comments c
            WHERE c.parent_comment_id = :parentId

            UNION ALL

            -- 재귀 부분: 각 레벨의 자식들을 연속해서 선택
            SELECT c.*, h.depth + 1
            FROM comments c
            INNER JOIN CommentHierarchy h ON c.parent_comment_id = h.id
            WHERE h.depth < 10
        )
        SELECT DISTINCT c.*, u.* 
        FROM CommentHierarchy c
        JOIN users u ON c.user_id = u.id
        ORDER BY c.created_date
    """, nativeQuery = true)
    List<Comment> findAllChildrenHierarchy(@Param("parentId") Long parentId);

    // 최상위 댓글 페이징 조회
    Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);







}
