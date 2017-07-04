package org.mousephenotype.ri.extract.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.UrlUtils;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.ImitsStatus;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.extract.Downloader;
import org.mousephenotype.ri.extract.GeneLoader;
import org.mousephenotype.ri.extract.GeneProcessor;
import org.mousephenotype.ri.extract.GeneWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/ri.application.properties")
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        EmbeddedServletContainerAutoConfiguration.class
})
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${GeneStatusChangeUrl}")
    protected String geneStatusChangeUrl;

    @NotNull
    @Value("${download.workspace}")
    protected String downloadWorkspace;

    private enum DownloadFileEnum {
        GENE_STATUS_CHANGE
    }

    public DownloadFilename[] filenames;


    @PostConstruct
    public void initialise() {
        filenames = new DownloadFilename[] {
                new DownloadFilename(DownloadFileEnum.GENE_STATUS_CHANGE, geneStatusChangeUrl, downloadWorkspace + "/GeneStatusChange.tsv")
        };
    }


    public class DownloadFilename {
        public final DownloadFileEnum downloadFileEnum;
        public String sourceUrl;
        public final String targetFilename;

        public DownloadFilename(DownloadFileEnum downloadFileEnum, String sourceUrl, String targetFilename) {
            this.downloadFileEnum = downloadFileEnum;
            this.sourceUrl = sourceUrl;
            this.targetFilename = targetFilename;
        }
    }


    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbc());
    }

    @NotNull
    @Value("${datasource.ri.url}")
    String riUrl;

    @NotNull
    @Value("${datasource.ri.username}")
    String username;

    @NotNull
    @Value("${datasource.ri.password}")
    String password;

    @Bean(name = "riDataSource", destroyMethod = "close")
    public DataSource riDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(riUrl)
                .username(username)
                .password(password)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);
        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {

            logger.info("Using database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) {

        }

        return ds;
    }

    @Bean
    public List<Downloader> downloader() {
        List<Downloader> downloaderList = new ArrayList<>();

        for (DownloadFilename download : filenames) {
            downloaderList.add(new Downloader(download.sourceUrl, download.targetFilename));
        }

        return downloaderList;
    }


    // LOADERS, PROCESSORS, AND WRITERS


    @Bean(name = "geneLoader")
    public GeneLoader geneLoader() throws InterestException {
        Map<GeneLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        Map<DownloadFileEnum, DownloadFilename> downloadFilenameMap = new HashMap<>();

        for (DownloadFilename downloadFilename : filenames) {

            downloadFilename.sourceUrl = UrlUtils.getRedirectedUrl(downloadFilename.sourceUrl);                         // Resolve any URL redirection.
            downloadFilenameMap.put(downloadFilename.downloadFileEnum, downloadFilename);
        }

        filenameKeys.put(GeneLoader.FilenameKeys.EBI_Gene, downloadFilenameMap.get(DownloadFileEnum.GENE_STATUS_CHANGE).targetFilename);

        return new GeneLoader(filenameKeys);
    }

    @Bean(name = "geneProcessor")
    public GeneProcessor geneProcessor() throws InterestException {

        Map<String, Gene> genesMap = sqlUtils().getGenes();
        Map<String, ImitsStatus> imitsStatusMap = sqlUtils().getImitsStatusMap();

        return new GeneProcessor(imitsStatusMap, genesMap);
    }

    @Bean(name = "geneWriter")
    public GeneWriter geneWriter() {
        return new GeneWriter();
    }
}