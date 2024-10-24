package com.example.sns_project.service;

// 댓글 관련 비즈니스 로직을 처리하는 서비스

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.UserDTO; // UserDTO 추가
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.Comment;
import com.example.sns_project.model.CommentLike;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;  // CommentRepository 의존성 주입
    private final PostRepository postRepository;        // PostRepository 의존성 주입
    private final UserRepository userRepository;        // UserRepository 의존성 주입
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // 댓글 작성
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO) {
        // 게시글 존재 여부 확인
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 댓글 생성
        Comment comment = new Comment();
        comment.setPost(post); // 댓글이 속한 게시글 설정
        comment.setContent(commentDTO.getContent());

        // User 객체를 가져와서 설정
        User user = userService.findById(commentDTO.getAuthorId()); // UserDTO에서 ID 가져오기
        comment.setUser(user); // User 객체 설정

        commentRepository.save(comment);  // 데이터베이스에 저장
        return new CommentDTO(comment.getId(), comment.getPost().getId(), comment.getContent(), user.getId());  // 작성한 댓글 반환
    }

    // 게시글에 대한 댓글 조회
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getPost().getId(), comment.getContent(), comment.getUser().getId())) // UserDTO의 ID 사용
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);  // 변경된 댓글 저장
        return commentDTO;  // 수정된 댓글 반환
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        commentRepository.delete(comment);  // 댓글 삭제
    }

    // 특정 사용자의 댓글 조회
    public List<CommentDTO> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId); // User ID로 댓글 조회
        return comments.stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getPost().getId(), comment.getContent(), comment.getUser().getId())) // UserDTO의 ID 사용
                .collect(Collectors.toList());
    }

    // 댓글 좋아요 기능 추가
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        comment.getLikes().add(commentLike); // 댓글의 좋아요 목록에 추가
        user.getLikedComments().add(commentLike); // 사용자의 댓글 좋아요 목록에 추가
        commentRepository.save(comment); // 변경사항 저장
    }

    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 좋아요를 찾고 제거
        CommentLike commentLike = comment.getLikes().stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User has not liked this comment"));

        comment.getLikes().remove(commentLike); // 댓글의 좋아요 목록에서 제거
        user.getLikedComments().remove(commentLike); // 사용자의 댓글 좋아요 목록에서 제거
        commentRepository.save(comment); // 변경사항 저장
    }

    // 대댓글
    @Transactional
    public CommentDTO addReply(Long parentCommentId, CommentDTO replyDTO) {
        // 부모 댓글 조회
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        // 대댓글 생성
        Comment replyComment = new Comment();
        replyComment.setContent(replyDTO.getContent());

        // User 객체를 가져와서 설정
        User user = userService.findById(replyDTO.getAuthorId()); // UserDTO에서 ID 가져오기
        replyComment.setUser(user); // User 객체 설정
        replyComment.setPost(parentComment.getPost()); // 같은 게시글에 속하도록 설정
        replyComment.setParentComment(parentComment); // 부모 댓글 설정

        // 부모 댓글의 대댓글 목록에 추가
        parentComment.getChildrenComments().add(replyComment);
        commentRepository.save(replyComment); // 대댓글 저장

        return replyDTO; // 생성된 대댓글 정보 반환
    }

    // 부모 댓글의 대댓글 조회
    public List<CommentDTO> getReplies(Long parentCommentId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        return parentComment.getChildrenComments().stream()
                .map(reply -> new CommentDTO(reply.getId(), reply.getPost().getId(), reply.getContent(), reply.getUser().getId())) // UserDTO의 ID 사용
                .collect(Collectors.toList());
    }

    // 앞으로: 댓글 좋아요 기능 추가
}
