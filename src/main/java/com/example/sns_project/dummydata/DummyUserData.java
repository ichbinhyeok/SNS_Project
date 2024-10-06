package com.example.sns_project.dummydata;

import com.example.sns_project.dto.UserDTO;
import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;

public class DummyUserData {

    private static final Faker faker = new Faker();

    public static List<UserDTO> generateDummyUsers(int count) {
        List<UserDTO> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserDTO user = new UserDTO();
            user.setUsername(faker.name().username());
            user.setPassword(faker.internet().password());
            user.setEmail(faker.internet().emailAddress());
            users.add(user);
        }
        return users;
    }
}
