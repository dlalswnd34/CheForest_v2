package com.simplecoding.cheforest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@EnableJpaAuditing
@EnableTransactionManagement
// TODO : JPA 사용 폴더 지정
@EnableJpaRepositories(basePackages = "com.simplecoding.cheforest.jpa")
// TODO : es(엘라스틱서치) 사용 폴더 지정
@EnableElasticsearchRepositories(basePackages = "com.simplecoding.cheforest.es")
@EnableScheduling
public class CheForestApplication { // extends SpringBootServletInitializer {

//    aws 배포시 필요
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(CheForestApplication.class); // WAR 배포 시 Spring Boot 앱 구동 설정
//    }

    public static void main(String[] args) {
        SpringApplication.run(CheForestApplication.class, args);
    }

}
