package com.example.sns_project.service;

// 댓글 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.model.Comment;
import com.example.sns_project.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;  // CommentRepository 의존성 주입

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        // 댓글 작성 로직
        Comment comment = new Comment();
        comment.setPostId(commentDTO.getPostId());
        comment.setContent(commentDTO.getContent());
        comment.setAuthorId(commentDTO.getAuthorId());
        commentRepository.save(comment);  // 데이터베이스에 저장
        return commentDTO;  // 작성한 댓글 반환
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        // 게시글에 대한 댓글 조회 로직
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getPostId(), comment.getContent(), comment.getAuthorId()))
                .collect(Collectors.toList());
    }

    // 앞으로: 댓글 수정, 삭제 메서드 추가
}
