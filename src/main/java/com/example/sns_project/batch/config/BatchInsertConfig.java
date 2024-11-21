package com.example.sns_project.batch.config;

import com.example.sns_project.batch.entity.TestEntity;
import com.example.sns_project.batch.listener.InsertJobListener;
import com.example.sns_project.batch.repository.TestEntityRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableBatchProcessing
public class BatchInsertConfig {

    @Autowired
    private org.springframework.batch.core.repository.JobRepository jobRepository;

    @Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    @Autowired
    private TestEntityRepository repository;



    @Bean
    public Job insertJob() {
        return new JobBuilder("insertJob", jobRepository)
                .start(insertStep())
                .listener(listener())
                .build();
    }

    @Bean
    public Step insertStep() {
        return new StepBuilder("insertStep", jobRepository)
                .<TestEntity, TestEntity>chunk(1000, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer(repository))
                .build();
    }

    @Bean
    public ItemReader<TestEntity> reader() {
        return new ItemReader<>() {
            private int count = 0;
            private final int limit = 1000000;

            @Override
            public TestEntity read() {
                if (count < limit) {
                    TestEntity entity = new TestEntity();
                    entity.setName("Batch Name : " + count++);
                    entity.setAge("Batch age : " + count++);
                    return entity;
                }
                return null;
            }
        };
    }

    @Bean
    public ItemProcessor<TestEntity, TestEntity> processor() {
        return item -> item;
    }

    @Bean
    public ItemWriter<TestEntity> writer(TestEntityRepository repository) {
        return repository::saveAll;
    }

    @Bean
    public JobExecutionListener listener() {
        return new InsertJobListener();
    }
}
