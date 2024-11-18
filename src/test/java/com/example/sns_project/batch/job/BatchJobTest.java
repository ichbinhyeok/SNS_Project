package com.example.sns_project.batch.job;

import com.example.sns_project.batch.config.BatchConfig;
import com.example.sns_project.batch.entity.InputType;
import com.example.sns_project.batch.dto.OutputType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@SpringBatchTest
public class BatchJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ItemProcessor<InputType, OutputType> itemProcessor;

    private List<InputType> inputTypeList;

    @BeforeEach
    public void setUp() {
        inputTypeList = new ArrayList<>();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        for (int i = 1; i <= 100; i++) {
            InputType input = new InputType();
            input.setData("Test Data " + i);
            inputTypeList.add(input);
            entityManager.persist(input);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    @DisplayName("Batch Job Test with Direct Processor Invocation")
    public void testBatchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        List<OutputType> outputList = new ArrayList<>();
        for (InputType inputType : inputTypeList) {
            OutputType outputType = itemProcessor.process(inputType);
            outputList.add(outputType);
        }

        stepExecution.getExecutionContext().put("outputList", outputList);
        assertThat(outputList).isNotNull();
        assertThat(outputList.size()).isEqualTo(10);

        for (OutputType output : outputList) {
            assertThat(output.getProcessedData()).startsWith("Processed: ");
        }
    }

    @Configuration
    static class TestConfig {
        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }
}
