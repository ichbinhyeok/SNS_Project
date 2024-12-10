package com.example.sns_project.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

//  배치 잡 실행 전후 처리
public class FakerListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(FakerListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("FakerJob 시작: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            logger.error("FakerJob 실패: {}", jobExecution.getJobInstance().getJobName());
        } else {
            logger.info("FakerJob 성공: {}", jobExecution.getJobInstance().getJobName());
        }
    }
}

