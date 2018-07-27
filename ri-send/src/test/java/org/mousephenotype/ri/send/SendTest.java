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

package org.mousephenotype.ri.send;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.utils.SqlUtils;
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
public class SendTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SqlUtils sqlUtils;


    /**
     * Sends an e-mail from no-reply@ebi.ac.uk to mrelac@ebi.ac.uk with subject 'my subject' and body 'my body'
     * to test send infrastructure
     *
     * @throws Exception
     */
@Ignore
    @Test
    public void testSendRegisterInterest() throws Exception {

        ApplicationSend app = new ApplicationSend(sqlUtils);
        context.getAutowireCapableBeanFactory().autowireBean(app);
        context.getAutowireCapableBeanFactory().initializeBean(app, "application");
        app.run();
    }
}