package com.example.sns_project.controller;

import com.example.sns_project.service.FakeDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeDataController {

    private final FakeDataService fakeDataService;

    public FakeDataController(FakeDataService fakeDataService) {
        this.fakeDataService = fakeDataService;
    }

    // 게시글 생성 요청
    @GetMapping("/generatePosts")
    public String generatePosts(@RequestParam int numberOfPosts) {
        try {
            fakeDataService.generatePosts(numberOfPosts);
            return "Posts generated successfully!";
        } catch (Exception e) {
            return "Error generating posts: " + e.getMessage();
        }
    }

    // 댓글 생성 요청
    @GetMapping("/generateComments")
    public String generateComments(@RequestParam int numberOfComments) {
        try {
            fakeDataService.generateComments(numberOfComments);
            return "Comments generated successfully!";
        } catch (Exception e) {
            return "Error generating comments: " + e.getMessage();
        }
    }
}
