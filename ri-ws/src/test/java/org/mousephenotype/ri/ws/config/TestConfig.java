package org.mousephenotype.ri.ws.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@ComponentScan(value = "org.mousephenotype.ri", excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AppConfig.class)})
public class TestConfig {
    @Bean
    public DataSource riDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("ri")
                .addScripts("sql/h2/schema.sql", "sql/h2/interestController-data.sql")
                .build();
    }
}