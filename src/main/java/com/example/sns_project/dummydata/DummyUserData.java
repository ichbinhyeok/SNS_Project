package com.example.sns_project.dummydata;

import com.example.sns_project.dto.UserRegistrationDTO; // UserRegistrationDTO 추가
import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;

public class DummyUserData {

    private static final Faker faker = new Faker();

    public static List<UserRegistrationDTO> generateDummyUsers(int count) {
        List<UserRegistrationDTO> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserRegistrationDTO user = new UserRegistrationDTO();
            user.setUsername(faker.name().username());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(faker.internet().password()); // 비밀번호 추가
            users.add(user);
        }
        return users;
    }
}
