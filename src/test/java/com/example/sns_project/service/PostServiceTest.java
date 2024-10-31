package com.example.sns_project.service;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.Comment;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.PostLike;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private PostService postService;
    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email");

        post = new Post();
        post.setId(1L);
        post.setTitle("Post Title");
        post.setContent("Post Content");
        post.setUser(user);

        // Mocking Comment
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
    }

    @Test
    @DisplayName("게시물 생성 테스트")
    void testCreatePost() {
        PostDTO postDTO = new PostDTO();
        postDTO.setContent(post.getContent());
        postDTO.setTitle(post.getTitle());
        postDTO.setAuthor(new UserDTO(user.getId(), user.getUsername(), user.getEmail()));

        when(userService.findById(user.getId())).thenReturn(user);

        PostDTO result = postService.createPost(postDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Post Title");
        assertThat(result.getContent()).isEqualTo("Post Content");
        assertThat(result.getAuthor().getUsername()).isEqualTo(user.getUsername());


        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    void testUpdatePost() {

        PostDTO updatePostDTO = new PostDTO();
        updatePostDTO.setContent("Update Content");
        updatePostDTO.setTitle("Update Title");

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));


        PostDTO result = postService.updatePost(post.getId(), updatePostDTO);
        assertThat(result.getTitle()).isEqualTo("Update Title");
        assertThat(result.getContent()).isEqualTo("Update Content");

        verify(postRepository).save(any(Post.class));
    }


    @Test
    @DisplayName("게시물 삭제 테스트")
    void testDeletePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        postService.deletePost(post.getId());
        verify(postRepository).delete(post);

    }

    @Test
    @DisplayName("게시물 삭제 실패 테스트 - Post not found")
    void testDeletePost_NotFound() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(post.getId()));
        assertThat(exception.getMessage()).isEqualTo("Post not found");
    }

    @Test
    @DisplayName("게시물 조회 테스트")
    void testGetPostById() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        PostDTO result = postService.getPostById(post.getId());
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Post Title");
        assertThat(result.getContent()).isEqualTo("Post Content");
        verify(postRepository).findById(post.getId());
    }

    @Test
    @DisplayName("특정 사용자 ID로 게시물 조회 테스트")
    void testGetPostByUserId() {
        when(postRepository.findByUserId(user.getId())).thenReturn(List.of(post));

        List<PostDTO> result = postService.getPostByUserId(user.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getContent()).isEqualTo("Post Content");

        verify(postRepository).findByUserId(user.getId());
    }


    @Test
    @DisplayName("게시물 좋아요 테스트")
    void testLikePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(user);

        postService.likePost(post.getId(), user.getId());

        verify(notificationService).sendPostLikeNotification(post.getUser().getId(), user.getUsername());
        verify(postRepository).save(post);

        assertThat(post.getLikes()).anyMatch(like -> like.getUser().equals(user) && like.getPost().equals(post));
        assertThat(user.getLikedPosts()).anyMatch(like -> like.getUser().equals(user) && like.getPost().equals(post));

    }

    @Test
    @DisplayName("게시물 좋아요 취소_성공")
    public void testUnlikePost() {
        // 게시물에 좋아요 추가
        post.getLikes().add(new PostLike(post, user));

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        postService.unlikePost(post.getId(), user.getId());

        assertThat(post.getLikes()).isEmpty();
    }


//    @Test
//    @DisplayName("댓글 추가_성공")
//    public void testAddComment() {
//    }
}
