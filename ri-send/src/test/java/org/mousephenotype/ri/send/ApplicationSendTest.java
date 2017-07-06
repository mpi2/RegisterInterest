package org.mousephenotype.ri.send;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.send.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by mrelac on 21/06/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
public class ApplicationSendTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SqlUtils sqlUtils;


    @Ignore
    @Test
    public void testSendRegisterInterest() throws Exception {

        ApplicationSend app = new ApplicationSend(sqlUtils);
        context.getAutowireCapableBeanFactory().autowireBean(app);
        context.getAutowireCapableBeanFactory().initializeBean(app, "application");
        app.run();
    }
}