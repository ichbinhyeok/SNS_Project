package com.example.sns_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.GroupedOpenApi;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api") // 그룹 이름 설정
                .pathsToMatch("/**") // 문서화할 경로 설정
                .build();
    }

    // 추가적인 설정이나 그룹을 만들 수 있습니다.
}
