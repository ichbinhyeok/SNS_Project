package com.example.sns_project.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//  배치 잡 실행 전후 처리

public class InsertJobListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(InsertJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job 시작: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            logger.error("Job 실패: {}", jobExecution.getJobInstance().getJobName());
        } else {
            logger.info("Job 성공: {}", jobExecution.getJobInstance().getJobName());
        }
    }
}

