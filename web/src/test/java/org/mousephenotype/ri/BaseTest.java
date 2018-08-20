package org.mousephenotype.ri;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

/**
 * Base test class to aggregate the spring test annotations.
 * All test classes should extend this base class
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {BaseTestConfig.class})
public class BaseTest {

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected DataSource riDataSource;

    @Before
    public void setup() throws Exception {

        Resource r = context.getResource("classpath:sql/schema.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
        r = context.getResource("classpath:sql/base-test-data.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
    }

    @Ignore
    @Test
    public void aTest() {

    }

}