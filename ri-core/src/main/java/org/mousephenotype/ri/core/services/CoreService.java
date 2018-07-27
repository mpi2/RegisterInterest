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

package org.mousephenotype.ri.core.services;

import org.mousephenotype.ri.core.entities.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class CoreService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private GenerateService generateService;
    private SendService sendService;

    @Inject
    public CoreService(GenerateService generateService, SendService sendService) {
        this.generateService = generateService;
        this.sendService = sendService;
    }

    public static void sleep(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);
        } catch (InterruptedException e) {

        }
    }
    public void generateAndSendWelcome(String emailAddress) {

        boolean inHtml = true;
        String welcomeText = generateService.getWelcomeContent(inHtml);
        sendService.sendWelcome(emailAddress, SendService.DEFAULT_WELCOME_SUBJECT, welcomeText, inHtml);
    }


    /**
     * Generate and send e-mails for all contacts who are registered for interest in any gene
     * @param showChangedGenes if true, include the 'changed gene' decoration (i.e. an asterisk next to each changed gene)
     *
     */
    public void generateAndSendAllSumaries(boolean showChangedGenes) {

        int count = 0;

        Map<String, Summary> summaries = generateService.getAllSummariesByEmailAddress();
        logger.info("BEGIN generateAndSendAllSummaries");

        for (Summary summary : summaries.values()) {

            generateAndSendSummary(summary, true, showChangedGenes);
            count++;

            // Pause for 36 seconds so we don't exceed 100 e-mails per hour.
            sleep(36);
        }

        logger.info("END generateAndSendAllSummaries. Processed {} summaries.", count);
    }


    /**
     * Generate and send e-mails for all contacts who have at least one gene of interest that has changed status since
     * the last e-mail was sent
     *
     */
    public void generateAndSendChangedSummaries() {

        int count = 0;

        Map<String, Summary> summaries = generateService.getChangedSummariesByEmailAddress();

        logger.info("BEGIN generateAndSendChangedSummaries");

        for (Summary summary : summaries.values()) {

            generateAndSendSummary(summary, true, true);
            count++;

            // Pause for 36 seconds so we don't exceed 100 e-mails per hour.
            sleep(36);
        }

        logger.info("END generateAndSendChangedSummaries. Processed {} summaries.", count);
    }


    /**
     * Generate and send a single summary e-mail
     *
     * @param summary Input instance
     * @param inHtml if true, generate output with html
     * @param showChangedGenes if true, include the 'changed gene' decoration (i.e. an asterisk next to each changed gene)
     */
    public void generateAndSendSummary(Summary summary, boolean inHtml, boolean showChangedGenes) {

        String content = generateService.getSummaryContent(summary, inHtml, showChangedGenes);
        sendService.sendSummary(summary, SendService.DEFAULT_SUMMARY_SUBJECT, content, inHtml);
    }
}