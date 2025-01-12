package com.example.sns_project.service;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.CommentHierarchyDTO;
import com.example.sns_project.exception.AlreadyLikedException;
import com.example.sns_project.exception.ApiException;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.exception.UnauthorizedException;
import com.example.sns_project.model.Comment;
import com.example.sns_project.model.CommentLike;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private static final int MAX_DEPTH = 10;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public CommentDTO createComment(Long parentCommentId, CommentDTO commentDTO) {
        User user = userService.findById(commentDTO.getAuthorId());
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUser(user);

        if (parentCommentId != null) {
            // 대댓글 작성
            Comment parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            // 깊이 제한 체크
            if (parentComment.getDepth() >= MAX_DEPTH) {
                throw new ApiException("댓글은 최대 " + MAX_DEPTH + "단계까지만 작성할 수 있습니다.");
            }

            comment.setPost(parentComment.getPost());
            parentComment.addChildComment(comment);
            // 대댓글 작성시 원 댓글 작성자에게 알림
            notificationService.sendCommentNotification(parentComment.getUser().getId(), user.getUsername());
        } else {
            // 최상위 댓글 작성
            Post post = postRepository.findById(commentDTO.getPostId())
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            comment.setPost(post);
            // 게시글 작성자에게 알림
            notificationService.sendCommentNotification(post.getUser().getId(), user.getUsername());
        }

        commentRepository.save(comment);
        return convertToDTO(comment);
    }

    @Transactional
    public CommentDTO updateComment(CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(commentDTO.getAuthorId())) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        return convertToDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentHierarchyDTO> getRootComments(Long postId, Pageable pageable) {
        Page<Comment> rootComments = commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable);

        List<CommentHierarchyDTO> hierarchyDTOs = rootComments.stream()
                .map(comment -> buildCommentHierarchy(comment, true, true)) // 루트 댓글임을 명시
                .collect(Collectors.toList());

        return new PageImpl<>(hierarchyDTOs, pageable, rootComments.getTotalElements());
    }

    // 특정 댓글의 모든 자식 댓글 조회 (페이징 없음)
    @Transactional(readOnly = true)
    public List<CommentHierarchyDTO> getAllChildComments(Long parentCommentId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        // 직계 자식 댓글들을 가져와서 각각의 전체 계층구조를 구성
        return parentComment.getChildrenComments().stream()
                .sorted(Comparator.comparing(Comment::getCreatedDate))
                .map(this::buildCommentHierarchyWithAllDescendants)
                .collect(Collectors.toList());
    }


    private CommentHierarchyDTO buildCommentHierarchy(Comment comment, boolean includeChildren, boolean isRoot) {
        CommentHierarchyDTO dto = new CommentHierarchyDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setContent(comment.getContent());
        dto.setAuthorId(comment.getUser().getId());
        dto.setAuthorName(comment.getUser().getUsername());
        dto.setCreatedAt(comment.getCreatedDate());
        dto.setDepth(comment.getDepth());

        if (includeChildren && !comment.getChildrenComments().isEmpty()) {
            List<CommentHierarchyDTO> replies;
            if (isRoot) {  // 루트 댓글인 경우에만 2개로 제한
                replies = comment.getChildrenComments().stream()
                        .sorted(Comparator.comparing(Comment::getCreatedDate))
                        .limit(2)  // 루트 댓글의 직계 자식만 2개로 제한
                        .map(child -> buildCommentHierarchy(child, false, false))  // 자식의 자식은 포함하지 않음
                        .collect(Collectors.toList());
            } else {
                replies = comment.getChildrenComments().stream()
                        .sorted(Comparator.comparing(Comment::getCreatedDate))
                        .map(child -> buildCommentHierarchy(child, false, false))
                        .collect(Collectors.toList());
            }
            dto.setReplies(replies);
        }

        dto.setTotalReplies(comment.calculateTotalReplies());
        return dto;
    }

    private CommentHierarchyDTO buildCommentHierarchyWithAllDescendants(Comment comment) {
        CommentHierarchyDTO dto = new CommentHierarchyDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setContent(comment.getContent());
        dto.setAuthorId(comment.getUser().getId());
        dto.setAuthorName(comment.getUser().getUsername());
        dto.setCreatedAt(comment.getCreatedDate());
        dto.setDepth(comment.getDepth());

        // 자식 댓글이 있는 경우 재귀적으로 처리
        if (!comment.getChildrenComments().isEmpty()) {
            List<CommentHierarchyDTO> childReplies = comment.getChildrenComments().stream()
                    .sorted(Comparator.comparing(Comment::getCreatedDate))
                    .map(this::buildCommentHierarchyWithAllDescendants)
                    .collect(Collectors.toList());
            dto.setReplies(childReplies);
        }

        dto.setTotalReplies(comment.calculateTotalReplies());
        return dto;
    }
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (comment.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userId))) {
            throw new AlreadyLikedException("Already liked this comment");
        }

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        comment.getLikes().add(commentLike);
        user.getLikedComments().add(commentLike);

        notificationService.sendCommentLikeNotification(comment.getUser().getId(), user.getUsername());
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

    private CommentDTO convertToDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getUser().getId()
        );
    }
}