package com.example.sns_project.batch.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class WeeklyReport {
    private Long customerId;        // 고객 ID
    private String customerName;    // 고객 이름
    private Double totalPurchases;  // 총 구매 금액
    private Date reportDate;        // 리포트 생성 날짜

    // 추가적인 필드가 필요하면 여기에 추가할 수 있습니다.
}
