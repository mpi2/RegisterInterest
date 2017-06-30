package org.mousephenotype.ri.extract;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.core.SqlUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * Created by mrelac on 22/05/2017.
 *
 * This class is intended to be a command-line callable java main program that loads the ri database with the lastest
 * iMits Gene GeneStatus Change Report.
 */
@EnableBatchProcessing
public class Application implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    @JobScope
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobRepository jobRepository;

    @Resource
    public List<Downloader> downloader;

    @Autowired
    public GeneLoader imitsLoader;

    @Autowired
    public NamedParameterJdbcTemplate jdbc;

    @Autowired
    @Qualifier("riDataSource")
    private DataSource riDataSource;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils(jdbc);



    @Override
    public void run(String... args) throws Exception {
        runJobs();
    }

    public Job[] runJobs() throws InterestException {

        // Populate Spring Batch tables if necessary.
        try {
            boolean exists = sqlUtils.columnInSchemaMysql(riDataSource.getConnection(), "BATCH_JOB_INSTANCE", "JOB_INSTANCE_ID");
            if ( ! exists) {
                sqlUtils.createSpringBatchTables(riDataSource);
            }

        } catch (Exception e) {
            throw new InterestException("Unable to create Spring Batch tables.");
        }

        Job[] jobs = new Job[]{
                downloaderJob(),
                imitsLoaderJob()
        };
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = dateFormat.format(new Date());

        for (int i = 0; i < jobs.length; i++) {
            Job job = jobs[i];
            try {
                JobInstance instance = jobRepository.createJobInstance("flow_" + now + "_" + i, new JobParameters());
                JobExecution execution = jobRepository.createJobExecution(instance, new JobParameters(), "jobExec_" + now + "_" + i);
                job.execute(execution);
            } catch (Exception e) {

                throw new InterestException(e);
            }
        }

        return jobs;
    }

    public Job downloaderJob() throws InterestException {

        List<Flow> flows = new ArrayList<>();
        for (int i = 0; i < downloader.size(); i++) {
            Downloader downloader = this.downloader.get(i);
            flows.add(new FlowBuilder<Flow>("subflow_" + i).from(downloader.getStep(stepBuilderFactory)).end());
        }

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("splitflow").start(flows.get(0));

        for (int i = 1; i < downloader.size(); i++) {
            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(Executors.defaultThreadFactory());
            flowBuilder.split(executor).add(flows.get(i));
        }

        return jobBuilderFactory.get("downloaderJob")
                .incrementer(new RunIdIncrementer())
                .start(flowBuilder.build())
                .end()
                .build();
    }

    public Job imitsLoaderJob() throws InterestException {

        // imits
        Flow geneFlow = new FlowBuilder<Flow>("geneFlow").from(imitsLoader).end();

        return jobBuilderFactory.get("geneLoaderJob")
                .incrementer(new RunIdIncrementer())
                .start(geneFlow)
                .end()
                .build();
    }
}