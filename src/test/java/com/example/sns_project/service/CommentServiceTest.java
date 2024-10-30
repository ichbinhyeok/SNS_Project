package com.example.sns_project.service;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.Comment;
import com.example.sns_project.model.CommentLike;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository; // 댓글 리포지토리 모킹
    @Mock
    private PostRepository postRepository; // 게시글 리포지토리 모킹
    @Mock
    private UserRepository userRepository; // 사용자 리포지토리 모킹
    @Mock
    private UserService userService; // 사용자 서비스 모킹
    @Mock
    private NotificationService notificationService; // 알림 서비스 모킹
    @InjectMocks
    private CommentService commentService; // 주입된 모킹을 사용하는 댓글 서비스

    private Post post; // 테스트용 게시글
    private User user; // 테스트용 사용자
    private Comment comment; // 테스트용 댓글

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화

        // 게시글 객체 초기화
        post = new Post();
        post.setTitle("Post Title");
        post.setContent("Post Content");
        post.setId(1L);

        // 사용자 객체 초기화
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");

        post.setUser(user); // 게시글과 사용자 연결

        // 댓글 객체 초기화
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Comment Content");
        comment.setPost(post);
        comment.setUser(user);
    }

    @Test
    @DisplayName("댓글 작성")
    public void testCreateComment_Success() {
        // 댓글 DTO 초기화
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setPostId(post.getId());
        commentDTO.setAuthorId(user.getId());
        commentDTO.setContent("Post Content");
        commentDTO.setId(1L);

        // 리포지토리의 반환값 설정
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(user);  // 사용자 서비스의 리턴값 설정

        // 댓글 생성 메서드 호출
        CommentDTO result = commentService.createComment(commentDTO);

        // 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getPostId()).isEqualTo(post.getId());
        assertThat(result.getAuthorId()).isEqualTo(user.getId());
        assertThat(result.getContent()).isEqualTo(commentDTO.getContent());

        // 리포지토리의 save 메서드 호출 검증
        verify(commentRepository).save(any(Comment.class));
        verify(notificationService).sendCommentNotification(post.getUser().getId(), user.getUsername());
    }

    @Test
    @DisplayName("댓글 조회")
    public void testGetCommentsByPostId() {
        // 댓글 리스트 초기화
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        when(commentRepository.findByPostId(post.getId())).thenReturn(commentList); // 댓글 리포지토리의 반환값 설정

        // 댓글 조회 메서드 호출
        List<CommentDTO> result = commentService.getCommentsByPostId(post.getId());

        // 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.get(0).getContent()).isEqualTo(comment.getContent());

        // 리포지토리의 findByPostId 메서드 호출 검증
        verify(commentRepository).findByPostId(post.getId());
    }

    @Test
    @DisplayName("댓글 수정")
    public void testUpdateComment_Success() {
        // 업데이트할 댓글 DTO 초기화
        CommentDTO newCommentDTO = new CommentDTO();
        newCommentDTO.setPostId(comment.getPost().getId());
        newCommentDTO.setAuthorId(comment.getUser().getId());
        newCommentDTO.setContent("Updated content");

        // 댓글 리포지토리의 반환값 설정
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // 댓글 수정 메서드 호출
        CommentDTO result = commentService.updateComment(comment.getId(), newCommentDTO);

        // 결과 검증
        assertThat(result.getContent()).isEqualTo("Updated content");
        verify(commentRepository).save(any(Comment.class)); // save 메서드 호출 검증
    }

    @Test
    @DisplayName("댓글 삭제_성공")
    public void testDeleteComment_Success() {
        // 댓글 리포지토리의 반환값 설정
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // 댓글 삭제 메서드 호출
        commentService.deleteComment(comment.getId());

        // delete 메서드 호출 검증
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제_실패_Comment not found")
    public void testDeleteComment_NotFound() {
        // 댓글이 없을 경우의 반환값 설정
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

        // 댓글 삭제 시 예외 발생 검증
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                commentService.deleteComment(comment.getId()));

        assertThat(exception.getMessage()).isEqualTo("Comment not found");
    }

    @Test
    @DisplayName("댓글 좋아요_성공")
    public void testLikeComment_Success() {
        // 댓글 및 사용자 리포지토리의 반환값 설정
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // 좋아요 초기화
        comment.setLikes(new HashSet<>());
        user.setLikedComments(new HashSet<>());

        // 댓글 좋아요 메서드 호출
        commentService.likeComment(comment.getId(), user.getId());

        // 좋아요 객체 검증
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLike.setId(1L);

        assertThat(comment.getLikes()).hasSize(1);
        assertThat(user.getLikedComments()).hasSize(1);

        // 알림 생성 검증
        verify(notificationService).sendCommentLikeNotification(comment.getUser().getId(), user.getUsername());

        // 추가: CommentLike 객체의 내용 검증
        CommentLike addedLike = comment.getLikes().iterator().next(); // Set에서 첫 번째 요소 가져오기
        assertThat(addedLike.getComment()).isEqualTo(comment);
        assertThat(addedLike.getUser()).isEqualTo(user);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 좋아요_실패 Comment not found")
    public void testLikeComment_CommentNotFound() {
        // 댓글이 없을 경우의 반환값 설정
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // 댓글 좋아요 시 예외 발생 검증
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.likeComment(comment.getId(), user.getId()));

        assertThat(exception.getMessage()).isEqualTo("Comment not found");
    }

    @Test
    @DisplayName("댓글 좋아요_실패 User not found")
    public void testLikeComment_UserNotFound() {
        // 사용자 없을 경우의 반환값 설정
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // 댓글 좋아요 시 예외 발생 검증
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.likeComment(comment.getId(), user.getId()));

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("댓글 좋아요 취소_성공")
    public void testUnlikeComment_Success() {
        // 좋아요 객체 생성 및 추가
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLike.setId(1L);

        comment.getLikes().add(commentLike); // 댓글에 좋아요 추가
        user.getLikedComments().add(commentLike); // 사용자에 좋아요 추가

        // 댓글 및 사용자 리포지토리의 반환값 설정
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // 댓글 좋아요 취소 메서드 호출
        commentService.unlikeComment(comment.getId(), user.getId());

        // 좋아요가 취소되었는지 검증
        assertThat(comment.getLikes()).hasSize(0);
        assertThat(user.getLikedComments()).hasSize(0);

        // 댓글 리포지토리의 save 메서드 호출 검증
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("대댓글 추가_성공")
    public void replyComment_Success() {
        // 댓글 리포지토리의 반환값 설정
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(userService.findById(user.getId())).thenReturn(user);

        // 대댓글 DTO 초기화
        CommentDTO replyCommentDTO = new CommentDTO();
        replyCommentDTO.setPostId(comment.getPost().getId());
        replyCommentDTO.setAuthorId(user.getId());
        replyCommentDTO.setContent("Reply Content");

        // 대댓글 추가 메서드 호출
        CommentDTO result = commentService.addReply(comment.getId(), replyCommentDTO);

        // 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Reply Content");
        assertThat(result.getAuthorId()).isEqualTo(user.getId());
        assertThat(result.getPostId()).isEqualTo(comment.getPost().getId());

        // 댓글 리포지토리의 save 메서드 호출 검증
        verify(commentRepository).save(any(Comment.class));

        // 대댓글이 추가되었는지 검증
        assertThat(comment.getChildrenComments().size()).isEqualTo(1);
        assertThat(comment.getChildrenComments().iterator().next().getContent()).isEqualTo(replyCommentDTO.getContent());
    }
}

