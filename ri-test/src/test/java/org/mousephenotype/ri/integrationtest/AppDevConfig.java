/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.ri.integrationtest;

import org.apache.commons.dbcp.BasicDataSource;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.generate.ApplicationGenerate;
import org.mousephenotype.ri.send.ApplicationSend;
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
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Created by mrelac on 27/06/2017. NOTE: 'dev' has been hard-coded in the PropertySource path below to help insure this test
 * is never run against the production database, as this test deletes all data from all tables except the gene table.
 */
@Configuration
@PropertySource(value="file:${user.home}/configfiles/dev/ri.application.properties")
@ComponentScan(value = {"org.mousephenotype.ri.integrationtest"} , excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.mousephenotype.ri.generate.config.AppConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.mousephenotype.ri.send.config.AppConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.mousephenotype.ri.test.config.AppConfig.class)
})
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


public class AppDevConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${ri-admin-password}")
    String riPassword;

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
        ((BasicDataSource) ds).setTestOnBorrow(true);
        ((BasicDataSource) ds).setValidationQuery("SELECT 1");

        try {

            logger.info("Using database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());
            logger.info("URL = " + riUrl);

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcRi() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbcRi());
    }

    @Bean
    public ApplicationGenerate applicationGenerate() {
        return new ApplicationGenerate(sqlUtils());
    }

    @Bean
    public ApplicationSend applicationSend() {
        return new ApplicationSend(sqlUtils());
    }

    @Bean
    public String riPassword() {
        return riPassword;
    }
}