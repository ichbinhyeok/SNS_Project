package com.example.sns_project.batch.entity;

import com.example.sns_project.batch.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sales")
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 판매 ID

    private Double amount; // 판매 금액

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; // 해당 판매의 고객
}
