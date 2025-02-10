package com.example.sns_project.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
public class UserDetailsDTO {
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public UserDetailsDTO(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role)
        );
    }
}