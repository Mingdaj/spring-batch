package com.jmd.batch.param;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MingDa
 * Time: 2024/3/22 15:34
 * File: ParamJob
 * Description: 作业参数
 */
@Configuration
public class ParamJob {

    //job构造器工厂
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //step构造器工厂
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    @Bean
//    public Tasklet tasklet(){
//        return new Tasklet() {
//            @Override
//            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//                System.out.println("SpringBatch....Param: " + jobParameters.get("name"));
//                return RepeatStatus.FINISHED;
//            }
//        };
//    }

    @StepScope
    @Bean
    public Tasklet tasklet(@Value("#{jobParameters['name']}")String name){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("SpringBatch....Param: " + name);
                return RepeatStatus.FINISHED;
            }
        };
    }
//    @Bean
//    public Step step1(){
//        return  stepBuilderFactory.get("step1")
//                .tasklet(tasklet())
//                .build();
//    }

    // step1调用tasklet实例方法时不需要传任何参数，Spring Boot 在加载Tasklet Bean实例时会自动注入
    @Bean
    public Step step1(){
        return  stepBuilderFactory.get("step1")
                .tasklet(tasklet(null))
                .build();
    }

//    @Bean
//    public Job job(){
//        return jobBuilderFactory.get("param-job")
//                .start(step1())
//                .build();
//    }

//    @Bean
//    public Job job(){
//        return jobBuilderFactory.get("param-chunk-job")
//                .start(step1())
//                .build();
//    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("param-value-job")
                .start(step1())
                .build();
    }
}
