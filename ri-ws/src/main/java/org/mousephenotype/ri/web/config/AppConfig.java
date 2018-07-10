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

package org.mousephenotype.ri.web.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

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
        Neo4jDataAutoConfiguration.class
})
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    // Database properties

    @NotNull
    @Value("${datasource.ri.password}")
    private String dbPassword;

    @NotNull
    @Value("${datasource.ri.username}")
    private String dbUsername;

    @NotNull
    @Value("${datasource.ri.url}")
    private String riUrl;

    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbc());
    }

    @Bean
    public DataSource riDataSource() {
        return SqlUtils.getConfiguredDatasource(riUrl, dbUsername, dbPassword);
    }


    // e-mail server properties

    @NotNull
    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @NotNull
    @Value("${mail.smtp.host}")
    private String smtpHost;

    @NotNull
    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @NotNull
    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;

    @Bean
    public String smtpFrom() {
        return smtpFrom;
    }

    @Bean
    public String smtpHost() {
        return smtpHost;
    }

    @Bean
    public int smtpPort() {
        return smtpPort;
    }

    @Bean
    public String smtpReplyto() {
        return smtpReplyto;
    }


    // phenotype archive properties


    @NotNull
    @Value("${riBaseUrl}")
    String riBaseUrl;

    @NotNull
    @Value("${paBaseUrl}")
    String paBaseUrl;

    @Bean
    public String paBaseUrl() {
        return paBaseUrl;
    }

    @Bean
    public String riBaseUrl() {
        return riBaseUrl;
    }


    // Environment properties

    @NotNull
    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Bean(name = "globalConfiguration")
    public Map<String, String> getGlobalConfig() {
        Map<String, String> map = new HashMap<>();
        map.put("riBaseUrl", riBaseUrl);
        map.put("paBaseUrl", paBaseUrl);
        return map;
    }

    @Bean
    public Map<String, String> config() {
        return config;
    }
}