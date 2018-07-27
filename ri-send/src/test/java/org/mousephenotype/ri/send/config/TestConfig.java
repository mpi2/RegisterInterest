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

import org.mousephenotype.ri.core.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Created by mrelac on 27/06/2017.
 */
@Configuration
@ComponentScan(value = {"org.mousephenotype.ri"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AppConfig.class)}
)
public class TestConfig {
    @Bean
    public DataSource riDataSource() {

        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .addScripts("sql/h2/schema.sql", "sql/h2/send-data.sql")
                .build();
    }

    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbc());
    }



    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${mail.smtp.host}")
    private String smtpHost;

    @NotNull
    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @NotNull
    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @NotNull
    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;


    @Bean
    public NamedParameterJdbcTemplate jdbcRi() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public String smtpHost() {
        return smtpHost;
    }

    @Bean
    public Integer smtpPort() {
        return smtpPort;
    }

    @Bean
    public String smtpFrom() {
        return smtpFrom;
    }

    @Bean
    public String smtpReplyto() {
        return smtpReplyto;
    }
}