package com.example.sns_project.batch.config;

import com.example.sns_project.batch.dto.WeeklyReport;
import com.example.sns_project.batch.entity.Customer;
import com.example.sns_project.batch.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
public class WeeklyReportBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CustomerRepository customerRepository;

    public WeeklyReportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomerRepository customerRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.customerRepository = customerRepository;
    }

    @Bean
    public Job weeklyReportJob() {
        return new JobBuilder("weeklyReportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(weeklyReportStep())
                .build();
    }

    @Bean
    public Step weeklyReportStep() {
        return new StepBuilder("weeklyReportStep", jobRepository)
                .<Customer, WeeklyReport>chunk(100, transactionManager)
                .reader(customerItemReader())
                .processor(weeklyReportProcessor())
                .writer(weeklyReportWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> customerItemReader() {
        List<Customer> customers = customerRepository.findAll();
        return new IteratorItemReader<>(customers);
    }

    @Bean
    public ItemProcessor<Customer, WeeklyReport> weeklyReportProcessor() {
        return customer -> {
            // WeeklyReport 생성 예시
            return WeeklyReport.builder()
                    .customerId(customer.getId())
                    .customerName(customer.getName())
                    .totalPurchases(customer.getTotalPurchases())
                    .reportDate(new Date())
                    .build();
        };
    }

    @Bean
    public ItemWriter<WeeklyReport> weeklyReportWriter() {
        return reports -> {
            for (WeeklyReport report : reports) {
                log.info("Weekly Report Created: {}", report);
                // 여기에서 DB 저장 로직을 추가할 수도 있음
            }
        };
    }
}
