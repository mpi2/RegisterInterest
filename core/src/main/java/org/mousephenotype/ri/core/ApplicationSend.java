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

package org.mousephenotype.ri.core;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.ri.core.services.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ApplicationSend implements CommandLineRunner {

    private final Logger       logger        = LoggerFactory.getLogger(this.getClass());
    private       CoreService  coreService;
    private       List<String> emailAddresses = new ArrayList<>();

    private final String[] OPT_ALL = {"a", "all"};
    private final String[] OPT_CHANGED = {"c", "changed"};
    private final String[] OPT_HELP = {"h", "help"};
    private final String[] OPT_SUMMARY = {"s", "summary"};
    private final String[] OPT_WELCOME = {"w", "welcome"};

    private boolean all     = false;
    private boolean changed = false;
    private boolean help    = false;
    private boolean summary = false;
    private boolean welcome = false;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApplicationSend.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Inject
    public ApplicationSend(CoreService coreService) {
        this.coreService = coreService;
    }


    public static final String USAGE = "Usage: [--help/-h] | [--all/-a] | [--changed/-c] | [--summary/-s email1 [email2 ...]] | [[--welcome/-w email1 [email2 ...]]]";

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        initialise(args);

        if (all) {
            logger.info("Generate and send gene status to ALL contacts.");
        } else if (changed) {
            logger.info("Generate and send gene status to contacts with gene status CHANGED since last e-mail.");
        } else if (summary) {
            for (String emailAddress : emailAddresses) {
                logger.info("Generate and send GENE STATUS e-mail to {}", emailAddress);
                coreService.generateAndSendSummary(emailAddress);
            }
        } else if (welcome) {
            for (String emailAddress : emailAddresses) {
                logger.info("Generate and send WELCOME e-mail to {}", emailAddress);
                coreService.generateAndSendWelcome(emailAddress);
            }
        }
    }


    // PRIVATE / PROTECTED METHODS


    private void initialise(String[] args) throws IOException {

        OptionParser parser  = new OptionParser();
        OptionSet    options = parseOptions(parser, args);

        logger.info("Program Arguments: " + StringUtils.join(args, ", "));

        if (help || args.length == 0) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        // all, changed, summary, and welcome are all mutually exclusive.
        int count = 0;
        if (all) count++;
        if (changed) count++;
        if (summary) count++;;
        if (welcome) count++;
        if (count > 1) {
            System.out.println("'all', 'changed', 'summary', and 'welcome' are mutually exclusive. Please specify only one.");
            System.out.println(usage());
            System.exit (1);
        }

        // neither all nor changed accept email addresses.
        if ((all || changed) && ( ! emailAddresses.isEmpty())) {
            System.out.println("'all' and 'changed'do not accept email addresses.");
            System.out.println(usage());
            System.exit (1);
        }

        // summary and welcome require at least one email address.
        if ((summary || welcome) && (emailAddresses.isEmpty())) {
            System.out.println("'summary' and 'welcome'require at least one email addresses.");
            System.out.println(usage());
            System.exit (1);
        }
    }

    protected OptionSet parseOptions(OptionParser parser, String[] args) {

        OptionSet options = null;

        parser.acceptsAll(Arrays.asList(OPT_HELP), "Display help/usage information\t" + USAGE)
                .forHelp();

        parser.acceptsAll(Arrays.asList(OPT_ALL), "Generate and send summary e-mail to all Register Interest contacts")
                .forHelp();

        parser.acceptsAll(Arrays.asList(OPT_CHANGED), "Generate and send summary e-mail to Register Interest contacts with one or more genes of interest whose status has changed since the last e-mail sent")
                .forHelp();

        parser.acceptsAll(Arrays.asList(OPT_SUMMARY), "Generate and send summary e-mail to specified contacts")
                .requiredIf("summary")
                .withRequiredArg()
                .describedAs("One or more email addresses, separated by whitespace")
                .forHelp();

        parser.acceptsAll(Arrays.asList(OPT_WELCOME), "Generate and send welcome e-mail to newly registered contacts")
                .requiredIf("welcome")
                .withRequiredArg()
                .describedAs("One or more email addresses, separated by whitespace")
                .forHelp();

        try {

            options = parser.parse(args);

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());
            System.out.println(usage());
            System.exit(1);
        }

        if (options.has("s"))
            emailAddresses.addAll((List<String>) options.valuesOf("s"));
        if (options.has("w"))
            emailAddresses.addAll((List<String>) options.valuesOf("w"));

        help = (options.has("help"));
        all = (options.has("all"));
        changed = (options.has("changed"));
        summary = (options.has("summary"));
        welcome = (options.has("welcome"));

        return options;
    }

    private String usage() {
        return USAGE;
    }
}