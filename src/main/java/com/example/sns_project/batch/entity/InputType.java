package com.example.sns_project.batch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 생성 전략 설정
    private Long id;
    private String data;

}
