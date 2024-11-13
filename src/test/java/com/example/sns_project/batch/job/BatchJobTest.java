package com.example.sns_project.batch.job;

import com.example.sns_project.batch.config.BatchConfig; // 배치 설정 클래스 임포트
import com.example.sns_project.batch.entity.InputType; // InputType 엔티티 임포트
import com.example.sns_project.batch.dto.OutputType; // OutputType DTO 임포트
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach; // JUnit 5의 테스트 준비 어노테이션
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test; // JUnit 5의 테스트 어노테이션
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer; // Job ID 증가기
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils; // Job 테스트 유틸리티
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired; // Spring의 의존성 주입 어노테이션
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // MockMvc 설정
import org.springframework.boot.test.context.SpringBootTest; // Spring Boot 테스트 어노테이션
import org.springframework.context.annotation.Import; // 설정 클래스 임포트
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 어노테이션

import java.util.ArrayList;
import java.util.List; // List 임포트
import static org.assertj.core.api.Assertions.assertThat; // AssertJ Assertions 임포트

@SpringBatchTest
@SpringBootTest // Spring Boot 테스트 설정
@Import(BatchConfig.class) // BatchConfig 클래스 임포트
@AutoConfigureMockMvc // MockMvc 자동 구성
public class BatchJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ItemProcessor<InputType, OutputType> itemProcessor; // itemProcessor 주입


    List<InputType> inputTypeList = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        //EntityManager : JPA에서 데이터베이스와의 상호작용을 처리하는 객체
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();


        // 테스트 데이터 생성
        for (int i = 1; i <= 10; i++) {
            InputType input = new InputType();
            input.setData("Test Data " + i); // ID 설정 없이 데이터만 설정
            inputTypeList.add(input);
            entityManager.persist(input); // 각 객체를 데이터베이스에 저장
        }
        entityManager.getTransaction().commit();
        entityManager.close();

    }


    @Test
    @DisplayName("배치 잡 테스트 Processor 직접 작성")
    public void testBatchJob() throws Exception {
        // JobParameters : Job 실행 시 다양한 설정을 위해 사용
        // 구분하기 쉽게 하기 위해서 현재 시간을 잡파라미터로 설정
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        /*
        Job 실행 및 결과 가져오기
        jobLauncherTestUtils를 사용하여 생성한 jobParameters로 배치 작업을 실행합니다.
        launchJob() 메서드는 배치 작업을 시작하고 JobExecution 객체를 반환하여 실행 결과를 확인할 수 있게 합니다.
        */
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Job 실행 상태 검증, 배치 작업이 성공적으로 완료되었음을 나타냄
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        /*
        JobExecution에서 실행된 스텝(Step)의 정보를 가져옵니다.
        스텝은 배치 작업의 작은 단위로, 여러 개의 스텝으로 구성될 수 있습니다.
        */
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        // ExecutionContext에 임시 데이터 설정
        List<OutputType> outputList = new ArrayList<>();

//         itemProcessor를 사용하여 InputType을 OutputType으로 변환
        for (InputType inputType : inputTypeList) {
            OutputType outputType = itemProcessor.process(inputType);
            outputList.add(outputType);
        }




        /*
        변환된 outputList를 stepExecution의 ExecutionContext에 "outputList"라는 키로 저장.
        이는 나중에 다른 스텝에서 사용할 수 있도록 데이터를 공유하기 위함
        */
        stepExecution.getExecutionContext().put("outputList", outputList);

        // OutputType 데이터 검증
        assertThat(outputList).isNotNull();
        assertThat(outputList.size()).isEqualTo(10); // 10개가 처리되어야 함

        for (OutputType output : outputList) {
            assertThat(output.getProcessedData()).startsWith("Processed: ");
        }
    }


    @Test
    @DisplayName("배치 잡 테스트 Processor 직접 작성 안함")
    public void testBatchJobWithNoProcessor() throws Exception {
        // JobParameters : Job 실행 시 다양한 설정을 위해 사용
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // Job 실행 및 결과 가져오기
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Job 실행 상태 검증
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        // JobExecution에서 실행된 Step의 정보를 가져오기
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        // ExecutionContext에서 변환된 OutputType 리스트 가져오기
        List<OutputType> outputList = (List<OutputType>) stepExecution.getExecutionContext().get("outputList");

        // OutputType 데이터 검증
        assertThat(outputList).isNotNull();
        assertThat(outputList.size()).isEqualTo(10); // 10개가 처리되어야 함

        for (OutputType output : outputList) {
            assertThat(output.getProcessedData()).startsWith("Processed: ");
        }
    }

}

