package com.example.sns_project.repository;

// 게시글 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long authorId);  // user ID로 게시글 조회
    List<Post> findAllByOrderByCreatedDateDesc(); // 최신 게시글 정렬


    @Query("SELECT COUNT(p) as totalPosts, " +
            "COUNT(DISTINCT c) as totalComments, " +
            "COALESCE(AVG(SIZE(p.likes)), 0) as averagePostLikes, " +
            "COALESCE(AVG(SIZE(c.likes)), 0) as averageCommentLikes " +
            "FROM Post p LEFT JOIN p.comments c")
    Object[] getPostStatistics();  // Map 대신 Object[] 반환


    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH p.likes pl " +
            "LEFT JOIN FETCH c.likes cl")
    List<Post> findAllWithCommentsAndLikes();

    @Query("SELECT p.id FROM Post p")
    List<Long> findPostIdsByPage(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id IN :ids")
    List<Post> findAllWithLikesByIds(@Param("ids") List<Long> ids);


    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 게시글 삭제 등)
}
