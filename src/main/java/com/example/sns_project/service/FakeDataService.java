package com.example.sns_project.service;

import com.example.sns_project.model.Comment;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FakeDataService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EntityManager entityManager;

    public FakeDataService(UserRepository userRepository, PostRepository postRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.entityManager = entityManager;
    }

    // 게시글 생성 메소드
    @Transactional
    public void generatePosts(int numberOfPosts) {
        int batchSize = 50;
        List<User> users = userRepository.findAll();  // 사용자 리스트 미리 캐싱

        for (int i = 0; i < numberOfPosts; i++) {
            Faker faker = new Faker();  // 각 스레드마다 Faker 객체 생성
            User user = users.get(faker.number().numberBetween(0, users.size()));
            Post post = new Post();
            post.setTitle(faker.lorem().sentence());
            post.setContent(faker.lorem().sentence(10));
            post.setUser(user);

            // 게시글 저장
            entityManager.persist(post);

            // 일정 간격마다 flush 및 clear
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        // 최종적으로 배치된 데이터 저장
        entityManager.flush();
        entityManager.clear();
    }

    // 댓글 생성 메소드
    @Transactional
    public void generateComments(int numberOfComments) {
        int batchSize = 50;
        List<Post> posts = postRepository.findAll();  // 게시글 리스트 미리 캐싱
        List<User> users = userRepository.findAll();  // 사용자 리스트 미리 캐싱

        for (int i = 0; i < numberOfComments; i++) {
            Faker faker = new Faker();  // 각 스레드마다 Faker 객체 생성
            Post post = posts.get(faker.number().numberBetween(0, posts.size()));
            User user = users.get(faker.number().numberBetween(0, users.size()));

            Comment comment = new Comment();
            comment.setPost(post);
            comment.setUser(user);
            comment.setContent(faker.lorem().sentence());

            // 댓글 저장
            entityManager.persist(comment);

            // 랜덤 대댓글 생성
            int numberOfReplies = faker.number().numberBetween(0, 10);  // 대댓글 수
            for (int j = 0; j < numberOfReplies; j++) {
                Comment reply = new Comment();
                reply.setPost(post);
                reply.setUser(user);
                reply.setContent(faker.lorem().sentence());
                reply.setParentComment(comment);  // 부모 댓글 설정

                // 대댓글 저장
                entityManager.persist(reply);
            }

            // 일정 간격마다 flush 및 clear
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        // 최종적으로 배치된 데이터 저장
        entityManager.flush();
        entityManager.clear();
    }
}
