/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.ri.web.controller;

import org.mousephenotype.ri.BaseTestConfig;
import org.mousephenotype.ri.core.utils.SqlUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.web.FilterChainProxy;

import javax.sql.DataSource;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
//@ComponentScan("org.mousephenotype.ri")
//@ComponentScan(value = "org.mousephenotype.ri", excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AppConfig.class)})
public class TestConfig extends BaseTestConfig {
//    @Bean
//    public DataSource riDataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .ignoreFailedDrops(true)
//                .setName("ri")
//                .addScripts("sql/h2/schema.sql", "sql/h2/interestController-data.sql")
//                .build();
//    }

//    @Bean
//    SqlUtils sqlUtils() {
//        return new SqlUtils(jdbc());
//    }
//
//    @Bean
//    NamedParameterJdbcTemplate jdbc() {
//        return new NamedParameterJdbcTemplate(riDataSource());
//    }

    @Bean
    public InterestController interestController() {
        return new InterestController(sqlUtils());
    }

    @Bean
    public FilterChainProxy springSecurityFilterChain() {
        return new FilterChainProxy();
    }
}