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

package org.mousephenotype.ri.integrationtest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by mrelac on 27/06/2017.
 */
@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/test.properties")
//@ComponentScan("org.mousephenotype.ri")
public class TestConfig {

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Bean
//    public InterestController interestController() {
//        return new InterestController(sqlUtils());
//    }
//
//    @Bean
//    public DataSource riDataSource() {
//
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .ignoreFailedDrops(true)
//                .build();
//    }
//
//    @Bean
//    public NamedParameterJdbcTemplate jdbcRi() {
//        return new NamedParameterJdbcTemplate(riDataSource());
//    }
//
//    @Bean
//    public SqlUtils sqlUtils() {
//        return new SqlUtils(jdbcRi());
//    }
//
//    @Bean
//    public ApplicationGenerate applicationGenerate() {
//        return new ApplicationGenerate(sqlUtils());
//    }
//
//    @Bean
//    public ApplicationGenerateSummary applicationGenerateSummary() {
//        return new ApplicationGenerateSummary(sqlUtils());
//    }
//
//    @Bean
//    public ApplicationSend applicationSend() {
//        return new ApplicationSend(sqlUtils());
//    }
}