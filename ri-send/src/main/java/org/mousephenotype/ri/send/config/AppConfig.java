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

package org.mousephenotype.ri.send.config;

import org.mousephenotype.ri.core.SqlUtils;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
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
    @Value("${datasource.ri.url}")
    String riUrl;

    @NotNull
    @Value("${datasource.ri.username}")
    String dbUsername;

    @NotNull
    @Value("${datasource.ri.password}")
    String dbPassword;


//    @NotNull
//    @Value("${mail.smtp.host}")
//    private String smtpHost;
//
//    @NotNull
//    @Value("${mail.smtp.port}")
//    private Integer smtpPort;
//
//    @NotNull
//    @Value("${mail.smtp.from}")
//    private String smtpFrom;
//
//    @NotNull
//    @Value("${mail.smtp.replyto}")
//    private String smtpReplyto;


    @Bean
    public DataSource riDataSource() {
        return SqlUtils.getConfiguredDatasource(riUrl, dbUsername, dbPassword);
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbcRi());
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcRi() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }


//    @Bean
//    public String smtpHost() {
//        return smtpHost;
//    }
//
//    @Bean
//    public Integer smtpPort() {
//        return smtpPort;
//    }
//
//    @Bean
//    public String smtpFrom() {
//        return smtpFrom;
//    }
//
//    @Bean
//    public String smtpReplyto() {
//        return smtpReplyto;
//    }
}