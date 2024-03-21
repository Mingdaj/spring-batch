<a name="E7NhI"></a>
### 导学
官网介绍：[https://docs.spring.io/spring-batch/reference/spring-batch-intro.html](https://docs.spring.io/spring-batch/reference/spring-batch-intro.html)<br />批处理就是将数据分批次进行处理的过程。<br />常规的批处理操作步骤：系统A从数据库中导出数据到文件，系统B读取文件数据并写入到数据库<br />批处理特点：

- 自动执行，根据系统设定的工作步骤自动完成
- 数据量大，少则百万，多则上千万甚至上亿。(如果是10亿，100亿那只能上大数据了)
- 定时执行，比如：每天，每周，每月执行。
<a name="n5gSz"></a>
#### 使用场景
典型的批处理程序通常：

- 从数据库、文件或队列中读取大量记录。
- 以某种方式处理数据。
- 以修改后的形式写回数据。

Spring Batch 自动化了这个基本的批处理迭代，提供了将类似事务作为一个集合进行处理的能力，通常是在离线环境中，无需任何用户交互。批处理作业是大多数 IT 项目的一部分，Spring Batch 是唯一提供强大的企业级解决方案的开源框架。
<a name="goKLC"></a>
#### 业务场景
Spring Batch支持以下业务场景：

- 定期提交批处理过程。
- 并行批处理：对作业进行并行处理。
- 分阶段、企业消息驱动的处理。
- 大规模并行批处理。
- 故障后手动或计划重新启动。
- 相关步骤的顺序处理（扩展到工作流驱动的批处理）。
- 部分处理：跳过记录（例如，在回滚时）。
- 整个批处理事务，适用于具有小批量大小或现有存储过程或脚本的情况。
<a name="dUrsJ"></a>
### 内存版
需求：打印一个Hello Spring Batch!不带读、写和处理。

1. 创建一个干净的maven项目
2. 在pom.xml中添加以下依赖：
```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.3</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-batch</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- 内存版 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

3. 创建一个Spring Boot启动类：
```java
@SpringBootApplication
// 加上@EnableBatchProcessing注解后，SpringBoot会自动加载 JobLauncher JobBuilderFactory StepBuilderFactory 类并创建对象交给容器管理，要使用时，直接注入即可
@EnableBatchProcessing
public class SpringBatchApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(SpringBatchApplication.class, args);
    }
}
```

4. 定义一个任务：
```java
@Configuration
public class QuickStartH2 {

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
        return jobBuilderFactory.get("job-h2")
                .start(step1())
                .build();
    }
}
```
批处理允许重复执行，异常重试，此时需要保存批处理状态与数据，Spring Batch 将数据缓存在H2内存中或者缓存在指定数据库中。

5. 启动项目：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/35841438/1711000310079-4399a83c-845c-4fa1-907d-aedb7adb39df.png#averageHue=%23232531&clientId=u523c04cf-a338-4&from=paste&height=330&id=ubcd3925e&originHeight=330&originWidth=1493&originalType=binary&ratio=1&rotation=0&showTitle=false&size=121043&status=done&style=none&taskId=u6f9cffa2-c74a-4a1d-9886-bbb2cf59fa8&title=&width=1493)
<a name="bZ26a"></a>
### MySQL版

1. 创建spring_batch数据库：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/35841438/1711000482298-226cac9e-8dfc-4006-87db-9e6240e86c7d.png#averageHue=%23efeeed&clientId=u523c04cf-a338-4&from=paste&height=202&id=u3d76c00e&originHeight=202&originWidth=381&originalType=binary&ratio=1&rotation=0&showTitle=false&size=8206&status=done&style=none&taskId=u6c85b8f4-ab8f-4faf-bbce-a44a4b6ab4e&title=&width=381)

2. 将pom.xml中的h2依赖注释掉
3. application.yml文件配置数据库连接和初始化SQL脚本

**注意：**<br />sql.init.model 第一次启动为always， 后面启动需要改为never，否则每次执行SQL都会异常。第一次启动会自动执行指定的脚本，后续不需要再初始化。
```yaml
spring:
  datasource:
    username: root
    password: admin
    url: jdbc:mysql://127.0.0.1:3309/spring_batch?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
  # 初始化数据库，sql脚本在依赖jar中
  sql:
    init:
      mode: always
      schema-locations: classpath:org/springframework/batch/core/schema-mysql.sql
#      mode: never
```

4. 定义一个任务：
```java
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
```

5. 启动项目：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/35841438/1711001226084-7ce2739d-e191-47ba-860b-796d4692a92b.png#averageHue=%23242532&clientId=u523c04cf-a338-4&from=paste&height=349&id=uf8d8e414&originHeight=349&originWidth=1512&originalType=binary&ratio=1&rotation=0&showTitle=false&size=120931&status=done&style=none&taskId=ua2339989-9e41-421d-80a0-8f089917542&title=&width=1512)

6. 数据库：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/35841438/1711001329885-c295a35c-6c36-4c09-8236-ad9b3a00589d.png#averageHue=%23faf8f6&clientId=u523c04cf-a338-4&from=paste&height=223&id=u3edf7982&originHeight=223&originWidth=415&originalType=binary&ratio=1&rotation=0&showTitle=false&size=11945&status=done&style=none&taskId=uc37c0e99-20a7-4504-8e5b-1cd266dcd1c&title=&width=415)<br />![image.png](https://cdn.nlark.com/yuque/0/2024/png/35841438/1711001530849-1bfaa8b9-a112-44b1-84c3-716ad987cf3f.png#averageHue=%23f5f5f5&clientId=u523c04cf-a338-4&from=paste&height=297&id=u08029b65&originHeight=297&originWidth=749&originalType=binary&ratio=1&rotation=0&showTitle=false&size=20425&status=done&style=none&taskId=u5dd599cb-92e9-451b-af66-b7ecb05c96c&title=&width=749)
