package org.mousephenotype.ri.generate;

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.ContactGene;
import org.mousephenotype.ri.core.entities.Sent;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.generate.config.AppConfig;
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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * Created by mrelac on 05/06/2017.
 *
 * This class is intended to be a command-line callable java main program that generates e-mails to contacts registered
 * for insterest in specific genes whose status indicates the gene state has changed.
 */
@SpringBootApplication
@ComponentScan({"org.mousephenotype"})
@Import( {AppConfig.class })
public class Application implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Autowired
    public NamedParameterJdbcTemplate jdbc;

    @Autowired
    @Qualifier("riDataSource")
    private DataSource riDataSource;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils(jdbc);


    @Override
    public void run(String... args) throws Exception {

        List<ContactGene> contactGenes = null;

        for (ContactGene contactGene : contactGenes) {

            // Determine
        }


        logger.info("Hello, World!");
    }

//    public Sent generateEmail(ContactGene contactGene) {
//
//
//        Sent sent;
//
//        return sent;
//
//    }
}