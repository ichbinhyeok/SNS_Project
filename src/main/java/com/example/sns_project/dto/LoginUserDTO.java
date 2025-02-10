package com.example.sns_project.dto;

import lombok.Getter;

@Getter
public class LoginUserDTO {
    private final Long id;
    private final String username;
    private final String password;

    public LoginUserDTO(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}