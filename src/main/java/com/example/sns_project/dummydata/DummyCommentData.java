package com.example.sns_project.dummydata;

import com.example.sns_project.dto.CommentDTO;
import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;

public class DummyCommentData {

    private static final Faker faker = new Faker();

    public static List<CommentDTO> generateDummyComments(int count) {
        List<CommentDTO> comments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CommentDTO comment = new CommentDTO();
            comment.setContent(faker.lorem().sentence());
            comments.add(comment);
        }
        return comments;
    }
}
