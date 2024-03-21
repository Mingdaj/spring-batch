package com.jmd.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created with IntelliJ IDEA.
 * User: MingDa
 * Time: 2024/3/21 13:33
 * File: SpringBatchApplication
 * Description: 启动类
 */
@SpringBootApplication
// 加上@EnableBatchProcessing注解后，SpringBoot会自动加载 JobLauncher JobBuilderFactory StepBuilderFactory 类并创建对象交给容器管理，要使用时，直接注入即可
@EnableBatchProcessing
public class SpringBatchApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(SpringBatchApplication.class, args);
    }
}
