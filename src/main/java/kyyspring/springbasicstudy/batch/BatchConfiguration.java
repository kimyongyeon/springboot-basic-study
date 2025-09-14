package kyyspring.springbasicstudy.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
@Slf4j
public class BatchConfiguration {
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Value("${chunk.size:1000}")
    private int chunkSize;

    @Value("${thread.count:4}")
    private int threadCount;

    public BatchConfiguration(EntityManagerFactory entityManagerFactory,
                              JobRepository jobRepository,
                              PlatformTransactionManager transactionManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job parallelStepsJob() throws Exception {
        Flow flow1 = new FlowBuilder<Flow>("flow1")
                .start(partitionStep())
                .build();

        Flow flow2 = new FlowBuilder<Flow>("flow2")
                .start(step2())
                .build();

        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .split(taskExecutor())
                .add(flow1, flow2)
                .build();

        return new JobBuilder("parallelStepsJob", jobRepository)
                .start(parallelFlow)
                .end()
                .build();
    }

    @Bean
    public Step partitionStep() throws Exception {
        return new StepBuilder("partitionStep", jobRepository)
                .partitioner("slaveStep", partitioner())
                .step(slaveStep())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(reader2(null))
                .writer(writer())
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources;
        try {
            resources = resolver.getResources("classpath:data/*.csv");
            log.info("Found {} resources", resources.length);
            for (Resource resource : resources) {
                log.info("Resource: {}", resource.getFilename());
            }
        } catch (IOException e) {
            log.error("Error reading resources", e);
            throw new RuntimeException("Unable to read input files", e);
        }
        partitioner.setResources(resources);
        return partitioner;
    }

    @Bean
    public Step slaveStep() {
        return new StepBuilder("slaveStep", jobRepository)
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(reader("sample-data.csv"))
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader(@Value("#{stepExecutionContext['fileName']}") String filename) {
        log.info("Reading file: {}", filename);
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("data/" + filename))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Person.class)
                .strict(false)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader2(@Value("#{stepExecutionContext['fileName']}") String filename) {
        log.info("Reading file for step2: {}", filename);
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader2")
                .resource(new ClassPathResource("data/" + filename))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Person.class)
                .strict(false)
                .build();
    }

    @Bean
    public JpaItemWriter<Person> writer() {
        return new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("spring_batch");
        executor.setConcurrencyLimit(threadCount);
        return executor;
    }
}
