package org.mousephenotype.ri.send;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.send.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

/**
 * Created by mrelac on 21/06/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
public class ApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SqlUtils sqlUtils;

    @Autowired
    @Qualifier("riDataSource")
    private DataSource ds;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Test
    public void testSendRegisterInterest() throws Exception {

        Application app = new Application();
        context.getAutowireCapableBeanFactory().autowireBean(app);
        context.getAutowireCapableBeanFactory().initializeBean(app, "application");
        app.run();
    }
}