package org.mousephenotype.ri.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.generate.ApplicationGenerate;
import org.mousephenotype.ri.send.ApplicationSend;
import org.mousephenotype.ri.test.config.AppConfig;
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
@ContextConfiguration(classes = AppConfig.class)
public class ApplicationIntegrationTest {

    @Autowired
    private ApplicationContext generateContext;

    @Autowired
    private ApplicationContext sendContext;

    @Autowired
    private SqlUtils sqlutils;


    @Ignore
    @Test
    public void testGenerateAndSendRegisterInterest() throws Exception {

        System.out.println();
        ApplicationGenerate generateApp = new ApplicationGenerate(sqlutils);
        generateContext.getAutowireCapableBeanFactory().autowireBean(generateApp);
        generateContext.getAutowireCapableBeanFactory().initializeBean(generateApp, "generateApp");



        ApplicationSend sendApp = new ApplicationSend(sqlutils);
        sendContext.getAutowireCapableBeanFactory().autowireBean(sendApp);
        sendContext.getAutowireCapableBeanFactory().initializeBean(sendApp, "sendApp");

        generateApp.run();
        sendApp.run();
    }
}