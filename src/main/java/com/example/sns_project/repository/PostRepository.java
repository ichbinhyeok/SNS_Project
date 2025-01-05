package com.example.sns_project.repository;

// 게시글 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    /**
     * 전체 인기 게시물 조회 쿼리
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.likes l " +
            "LEFT JOIN p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(DISTINCT l) DESC, COUNT(DISTINCT c) DESC")
    Page<Post> findPopularPosts(Pageable pageable);


    /**
     * 실시간 인기 게시물 조회 쿼리
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.likes l " +
            "LEFT JOIN p.comments c " +
            "WHERE l.createdDate >= :oneDayAgo OR c.createdDate >= :oneDayAgo " +
            "GROUP BY p.id, p.title, p.content, p.createdDate " +
            "ORDER BY (COUNT(DISTINCT CASE WHEN l.createdDate >= :oneDayAgo THEN l END) * 2 + " +
            "COUNT(DISTINCT CASE WHEN c.createdDate >= :oneDayAgo THEN c END)) DESC")
    List<Post> findHotPosts(LocalDateTime oneDayAgo, Pageable pageable);
    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 게시글 삭제 등)
}
