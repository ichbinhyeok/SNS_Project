package com.example.sns_project.batch.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동으로 증가하는 ID 설정
    private Long id;

    private String name;
    private String age;

}
