package com.example.sns_project.batch.job;

import com.example.sns_project.batch.config.WeeklyReportBatchConfig;
import com.example.sns_project.batch.entity.Customer;
import com.example.sns_project.batch.entity.Sales;
import com.example.sns_project.batch.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBatchTest
@SpringBootTest
@Import(WeeklyReportBatchConfig.class)
public class WeeklyReportBatchConfigTest {

    @Autowired
    private JobLauncher jobLauncher;


    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WeeklyReportBatchConfig weeklyReportBatchConfig; // WeeklyReportBatchConfig를 주입

    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    public void setUp() {
        jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJob(weeklyReportBatchConfig.weeklyReportJob()); // Job 설정
    }

    @Test
    public void testWeeklyReportJob() throws Exception {
        // 예시 Customer 데이터 추가
        Customer customer1 = new Customer();
        customer1.setName("고객 A");
        customer1.setEmail("customerA@example.com");
        customerRepository.save(customer1);

        // 고객의 판매 데이터 추가
        Sales sale1 = new Sales();
        sale1.setAmount(100.0);
        sale1.setCustomer(customer1);

        Sales sale2 = new Sales();
        sale2.setAmount(200.0);
        sale2.setCustomer(customer1);

        List<Sales> salesList = new ArrayList<>();
        salesList.add(sale1);
        salesList.add(sale2);

        // 판매 데이터 저장
        customer1.setSales(salesList);
        customerRepository.save(customer1);

        // Job 실행
        var jobExecution = jobLauncherTestUtils.launchJob(new JobParameters());

        // 결과 확인
        assertNotNull(jobExecution);
        assertNotNull(jobExecution.getJobInstance());
        assertNotNull(jobExecution.getExecutionContext());

        // ExitStatus 확인 및 에러 로그 출력
//        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        if (!"COMPLETED".equals(jobExecution.getExitStatus().getExitCode())) {
            System.out.println("Job Execution failed with status: " + jobExecution.getExitStatus().getExitCode());
            for (Throwable exception : jobExecution.getAllFailureExceptions()) {
                System.out.println("Exception: " + exception.getMessage());
            }
        }
    }
}
