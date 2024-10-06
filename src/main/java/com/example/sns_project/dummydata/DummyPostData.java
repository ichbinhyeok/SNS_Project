package com.example.sns_project.dummydata;

import com.example.sns_project.dto.PostDTO;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class DummyPostData {

    private static final Faker faker = new Faker();

    public static List<PostDTO> generateDummyPosts(int count) {
        List<PostDTO> posts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PostDTO post = new PostDTO();
            post.setTitle(faker.lorem().sentence());
            post.setContent(faker.lorem().paragraph());
            posts.add(post);
        }
        return posts;
    }
}


