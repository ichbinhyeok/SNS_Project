package com.example.sns_project.batch.config;

import com.example.sns_project.batch.dto.OutputType;
import com.example.sns_project.batch.entity.InputType;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration // 이 클래스가 Spring Configuration임을 나타냄
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository; // Job 정보를 관리하는 JobRepository 주입

    @Autowired
    private EntityManagerFactory entityManagerFactory; // JPA EntityManagerFactory 주입

    @Autowired
    private PlatformTransactionManager transactionManager; // 트랜잭션 관리 매니저 주입

    @Autowired
    private JobLauncher jobLauncher; // JobLauncher 주입

    public void launchJob() {
        try {
            jobLauncher.run(job(), new JobParameters()); // 배치 작업 실행
        } catch (Exception e) {
            log.error("배치 작업 실행 중 오류 발생: ", e); // 오류 로그 출력
        }
    }


    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer()) // RunIdIncrementer 추가
                .flow(step()) // step 메서드를 호출하여 Step 설정
                .end()
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step", jobRepository)
                .<InputType, OutputType>chunk(100, transactionManager) // Chunk 단위로 처리 (100개씩)
                .reader(itemReader()) // ItemReader를 설정 가공할 데이터를 읽어옴
                .processor(itemProcessor()) // ItemProcessor를 설정, 읽어온 데이터를 가공함
                .writer(itemWriter()) // ItemWriter를 설정, 가공한 데이터를 어찌할지
                .build(); // Step을 빌드하여 반환
    }

    @Bean
    public ItemReader<InputType> itemReader() {
        return new JpaPagingItemReaderBuilder<InputType>()
                .name("itemReader") // 리더 이름 설정
                .entityManagerFactory(entityManagerFactory) // EntityManagerFactory 설정
                .queryString("SELECT i FROM InputType i") // 읽어올 데이터의 JPQL 쿼리
                .pageSize(100) // 한 번에 읽을 페이지 크기 설정
                .build(); // ItemReader를 빌드하여 반환
    }

    @Bean
    public ItemProcessor<InputType, OutputType> itemProcessor() {
        return input -> new OutputType(input); // InputType 객체를 받아 OutputType으로 변환
    }

    @Bean
    public ItemWriter<OutputType> itemWriter() {
        return items -> {
            for (OutputType output : items) {
                // 결과를 로그로 출력
                log.info(" **배치 결과 작성 Writing output: {}", output.getProcessedData());
            }
        };
    }



}
