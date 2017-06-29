package org.mousephenotype.ri.generate.config;

import org.mousephenotype.ri.core.SqlUtils;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Created by mrelac on 27/06/2017.
 */
@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/ri.test.properties")
@ComponentScan(value = "org.mousephenotype.ri", excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AppConfig.class)})
public class TestConfig {    // ri
    @Bean
    public DataSource riDataSource() {

        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .addScripts("sql/h2/schema.sql", "sql/h2/generate-data.sql")
                .build();
    }

    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbc());
    }
}