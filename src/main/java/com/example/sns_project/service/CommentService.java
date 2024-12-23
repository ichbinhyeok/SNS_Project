package com.example.sns_project.service;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.exception.UnauthorizedException;
import com.example.sns_project.model.Comment;
import com.example.sns_project.model.CommentLike;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    // 댓글 작성
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO) {
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userService.findById(commentDTO.getAuthorId());

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(commentDTO.getContent());
        comment.setUser(user);

        commentRepository.save(comment);

        // 게시글 작성자에게 알림 전송
        notificationService.sendCommentNotification(post.getUser().getId(), user.getUsername());

        return new CommentDTO(comment.getId(), comment.getPost().getId(),
                comment.getContent(), user.getId());
    }

    // 댓글 수정
    @Transactional
    public CommentDTO updateComment(CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // 댓글 작성자 검증
        if (!comment.getUser().getId().equals(commentDTO.getAuthorId())) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);

        return new CommentDTO(comment.getId(), comment.getPost().getId(),
                comment.getContent(), comment.getUser().getId());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // 댓글 작성자 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    // 대댓글 추가
    @Transactional
    public CommentDTO addReply(Long parentCommentId, CommentDTO replyDTO) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        User user = userService.findById(replyDTO.getAuthorId());

        Comment replyComment = new Comment();
        replyComment.setContent(replyDTO.getContent());
        replyComment.setUser(user);
        replyComment.setPost(parentComment.getPost());
        replyComment.setParentComment(parentComment);

        parentComment.getChildrenComments().add(replyComment);
        commentRepository.save(replyComment);

        // 부모 댓글 작성자에게 알림 전송
        notificationService.sendCommentNotification(parentComment.getUser().getId(), user.getUsername());

        return new CommentDTO(replyComment.getId(), replyComment.getPost().getId(),
                replyComment.getContent(), user.getId());
    }

    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 자신의 댓글에 좋아요 방지
        if (comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot like your own comment");
        }

        // 중복 좋아요 체크
        if (comment.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userId))) {
            throw new IllegalStateException("Already liked this comment");
        }

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        comment.getLikes().add(commentLike);
        user.getLikedComments().add(commentLike);

        notificationService.sendCommentLikeNotification(comment.getUser().getId(), user.getUsername());
    }

    // 나머지 메서드들은 그대로 유지 (getCommentsByPostId, unlikeComment, getReplies)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getPost().getId(),
                        comment.getContent(), comment.getUser().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CommentLike commentLike = comment.getLikes().stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User has not liked this comment"));

        comment.getLikes().remove(commentLike);
        user.getLikedComments().remove(commentLike);
        commentRepository.save(comment);
    }

    public List<CommentDTO> getReplies(Long parentCommentId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        return parentComment.getChildrenComments().stream()
                .map(reply -> new CommentDTO(reply.getId(), reply.getPost().getId(),
                        reply.getContent(), reply.getUser().getId()))
                .collect(Collectors.toList());
    }
}