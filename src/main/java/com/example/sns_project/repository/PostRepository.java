package com.example.sns_project.repository;

// 게시글 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long authorId);  // user ID로 게시글 조회
    List<Post> findAllByOrderByCreatedDateDesc(); // 최신 게시글 정렬

    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 게시글 삭제 등)
}
