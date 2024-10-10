package com.example.sns_project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 사용자 ID

    @Column(nullable = false, unique = true)
    private String username;      // 사용자 이름

    @Column(nullable = false, unique = true)
    private String email;         // 사용자 이메일

    @Column(nullable = false)
    private String password;      // 사용자 비밀번호

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;     // 사용자의 역할 리스트
}
