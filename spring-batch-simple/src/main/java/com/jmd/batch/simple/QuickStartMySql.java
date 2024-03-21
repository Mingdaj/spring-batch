package com.jmd.batch.simple;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: MingDa
 * Time: 2024/3/21 13:39
 * File: QuickStartH2
 * Description: 入门案例，MySQL版
 */
@Configuration
public class QuickStartMySql {

    // job调度器
    @Autowired
    private JobLauncher jobLauncher;

    // job构造器工厂
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    // step构造器工厂
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 任务: step执行逻辑由tasklet实现
     * @return
     */
    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello Spring Batch!");
                return RepeatStatus.FINISHED;
            }
        };
    }

    /**
     * step: 执行逻辑(作业步骤)
     * @return
     */
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet())
                .build();
    }

    /**
     * job: 任务
     * @return
     */
    @Bean
    public Job job() {
        return jobBuilderFactory.get("job-MySQL")
                .start(step1())
                .build();
    }
}
