package com.example.sns_project.service;

// 게시글 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.Post;
import com.example.sns_project.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;  // PostRepository 의존성 주입

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostDTO createPost(PostDTO postDTO) {
        // 게시글 작성 로직
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthorId(postDTO.getAuthorId());
        postRepository.save(post);  // 데이터베이스에 저장
        return postDTO;  // 작성한 게시글 반환
    }

    public PostDTO getPostById(Long id) {
        // 게시글 조회 로직
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));  // 예외 처리
        return new PostDTO(post.getId(), post.getTitle(), post.getContent(), post.getAuthorId());
    }

    // 앞으로: 게시글 수정, 삭제, 목록 조회 메서드 추가
}
