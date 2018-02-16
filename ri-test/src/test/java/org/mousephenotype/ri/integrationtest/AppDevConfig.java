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

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.generate.ApplicationGenerate;
import org.mousephenotype.ri.generate.ApplicationGenerateSummary;
import org.mousephenotype.ri.send.ApplicationSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/application.properties")
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


    @Bean
    public DataSource riDataSource() {



        // FIXME

        riUrl = "jdbc:mysql://mysql-mi-dev:4356/ri?autoReconnect=true&amp;useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterEncoding=utf8&amp;characterSetResults=utf8&amp;zeroDateTimeBehavior=convertToNull";
        return SqlUtils.getConfiguredDatasource(riUrl, username, password);
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
    public ApplicationGenerateSummary applicationGenerateSummary() {
        return new ApplicationGenerateSummary(sqlUtils());
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