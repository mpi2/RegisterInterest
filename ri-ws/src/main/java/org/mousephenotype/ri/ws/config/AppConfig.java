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

package org.mousephenotype.ri.ws.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.mousephenotype.ri.core.SqlUtils;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

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
        Neo4jDataAutoConfiguration.class
})
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbc());
    }

    @Value("${datasource.ri.url}")
    String riUrl;

    @Value("${datasource.ri.username}")
    String username;

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
}