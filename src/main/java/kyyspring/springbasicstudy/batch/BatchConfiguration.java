package kyyspring.springbasicstudy.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BatchConfiguration {
//    private final EntityManagerFactory entityManagerFactory;
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//
//    @Value("${chunk.size:1000}")
//    private int chunkSize;
//
//    @Value("${thread.count:4}")
//    private int threadCount;
//
//    public BatchConfiguration(EntityManagerFactory entityManagerFactory,
//                              JobRepository jobRepository,
//                              PlatformTransactionManager transactionManager) {
//        this.entityManagerFactory = entityManagerFactory;
//        this.jobRepository = jobRepository;
//        this.transactionManager = transactionManager;
//    }
//
//    @Bean
//    public Job parallelStepsJob() throws Exception {
//        Flow flow1 = new FlowBuilder<Flow>("flow1")
//                .start(partitionStep())
//                .build();
//
//        Flow flow2 = new FlowBuilder<Flow>("flow2")
//                .start(step2())
//                .build();
//
//        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
//                .split(taskExecutor())
//                .add(flow1, flow2)
//                .build();
//
//        return new JobBuilder("parallelStepsJob", jobRepository)
//                .start(parallelFlow)
//                .end()
//                .build();
//    }
//
//    @Bean
//    public Step partitionStep() throws Exception {
//        return new StepBuilder("partitionStep", jobRepository)
//                .partitioner("slaveStep", partitioner())
//                .step(slaveStep())
//                .taskExecutor(taskExecutor())
//                .build();
//    }
//
//    @Bean
//    public Step step2() {
//        return new StepBuilder("step2", jobRepository)
//                .<Person, Person>chunk(chunkSize, transactionManager)
//                .reader(reader2(null))
//                .writer(writer())
//                .build();
//    }
//
//    @Bean
//    public Partitioner partitioner() {
//        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        Resource[] resources;
//        try {
//            resources = resolver.getResources("classpath:data/*.csv");
//            log.info("Found {} resources", resources.length);
//            for (Resource resource : resources) {
//                log.info("Resource: {}", resource.getFilename());
//            }
//        } catch (IOException e) {
//            log.error("Error reading resources", e);
//            throw new RuntimeException("Unable to read input files", e);
//        }
//        partitioner.setResources(resources);
//        return partitioner;
//    }
//
//    @Bean
//    public Step slaveStep() {
//        return new StepBuilder("slaveStep", jobRepository)
//                .<Person, Person>chunk(chunkSize, transactionManager)
//                .reader(reader("sample-data.csv"))
//                .writer(writer())
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public FlatFileItemReader<Person> reader(@Value("#{stepExecutionContext['fileName']}") String filename) {
//        log.info("Reading file: {}", filename);
//        return new FlatFileItemReaderBuilder<Person>()
//                .name("personItemReader")
//                .resource(new ClassPathResource("data/" + filename))
//                .delimited()
//                .names("firstName", "lastName")
//                .targetType(Person.class)
//                .strict(false)
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public FlatFileItemReader<Person> reader2(@Value("#{stepExecutionContext['fileName']}") String filename) {
//        log.info("Reading file for step2: {}", filename);
//        return new FlatFileItemReaderBuilder<Person>()
//                .name("personItemReader2")
//                .resource(new ClassPathResource("data/" + filename))
//                .delimited()
//                .names("firstName", "lastName")
//                .targetType(Person.class)
//                .strict(false)
//                .build();
//    }
//
//    @Bean
//    public JpaItemWriter<Person> writer() {
//        return new JpaItemWriterBuilder<Person>()
//                .entityManagerFactory(entityManagerFactory)
//                .build();
//    }
//
//    @Bean
//    public TaskExecutor taskExecutor() {
//        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("spring_batch");
//        executor.setConcurrencyLimit(threadCount);
//        return executor;
//    }
}
