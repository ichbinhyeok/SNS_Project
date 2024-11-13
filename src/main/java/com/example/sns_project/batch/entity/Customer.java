package com.example.sns_project.batch.entity;

import com.example.sns_project.batch.entity.Sales;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고객 ID

    private String name; // 고객 이름
    private String email; // 고객 이메일

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, ) // Sales와의 관계 설정
    private List<Sales> sales; // 고객의 구매 내역 초기화

    // 총 구매 금액을 계산하는 메소드
    public Double getTotalPurchases() {
        return sales.stream()
                .mapToDouble(Sales::getAmount) // 각 Sales의 금액을 가져와 합산
                .sum(); // 총합 반환
    }
}
