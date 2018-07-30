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

package org.mousephenotype.ri.reports.support;

import org.mousephenotype.ri.core.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * ReportType bean configuration
 */

@Configuration
@EnableAutoConfiguration
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class ReportsConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


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
}