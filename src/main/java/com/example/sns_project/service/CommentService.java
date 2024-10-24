package com.example.sns_project.service;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.enums.NotificationType;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.Comment;
import com.example.sns_project.model.CommentLike;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
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

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(commentDTO.getContent());

        User user = userService.findById(commentDTO.getAuthorId());
        comment.setUser(user);

        commentRepository.save(comment);

        // 알림 생성
        String message = user.getUsername() + "님이 당신의 댓글에 댓글을 달았습니다.";
        notificationService.sendNotification(post.getUser().getId(), message, NotificationType.COMMENT);
        return new CommentDTO(comment.getId(), comment.getPost().getId(), comment.getContent(), user.getId());
    }

    // 게시글에 대한 댓글 조회
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getPost().getId(), comment.getContent(), comment.getUser().getId()))
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        return new CommentDTO(comment.getId(), comment.getPost().getId(), comment.getContent(), comment.getUser().getId());
    }

    // 댓글 삭제
     @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        commentRepository.delete(comment); // 부모 댓글 삭제 시 대댓글도 함께 삭제됨
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
        comment.getLikes().add(commentLike);
        user.getLikedComments().add(commentLike);
        commentRepository.save(comment);

        // 알림 생성
        String message = user.getUsername() + "님이 당신의 댓글에 좋아요를 눌렀습니다.";
        notificationService.sendNotification(comment.getUser().getId(), message, NotificationType.LIKE);
    }

    // 대댓글 좋아요 기능 추가
    @Transactional
    public void likeReply(Long replyId, Long userId) {
        likeComment(replyId, userId); // 대댓글도 댓글과 동일한 방식으로 처리
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

    // 대댓글 추가
    @Transactional
    public CommentDTO addReply(Long parentCommentId, CommentDTO replyDTO) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        Comment replyComment = new Comment();
        replyComment.setContent(replyDTO.getContent());

        User user = userService.findById(replyDTO.getAuthorId());
        replyComment.setUser(user);
        replyComment.setPost(parentComment.getPost());
        replyComment.setParentComment(parentComment);

        parentComment.getChildrenComments().add(replyComment);
        commentRepository.save(replyComment);

        return new CommentDTO(replyComment.getId(), replyComment.getPost().getId(), replyComment.getContent(), user.getId());
    }

    // 부모 댓글의 대댓글 조회
    public List<CommentDTO> getReplies(Long parentCommentId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        return parentComment.getChildrenComments().stream()
                .map(reply -> new CommentDTO(reply.getId(), reply.getPost().getId(), reply.getContent(), reply.getUser().getId()))
                .collect(Collectors.toList());
    }
}

