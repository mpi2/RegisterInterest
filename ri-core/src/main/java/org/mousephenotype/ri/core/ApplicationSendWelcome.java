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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import java.util.Arrays;
import java.util.List;

public class ApplicationSendWelcome implements CommandLineRunner {

    private final Logger       logger        = LoggerFactory.getLogger(this.getClass());
    private       List<String> emailAddresses;
    private       boolean      helpRequested = false;
    private       OptionParser parser        = new OptionParser();

    private final String[] OPT_HELP = {"h", "help"};

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationSendWelcome.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    /**
     * Program arguments:
     *      -h, --help, or one or more email address arguments
     */
    @Override
    public void run(String... args) throws Exception {

        OptionSet options = parseOptions(args);

        logger.info("Program Arguments: " + StringUtils.join(args, ", "));

        if (helpRequested) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        if (emailAddresses.isEmpty()) {
            System.err.println("One or more e-mail addresses is required.");
            parser.printHelpOn(System.err);
            System.exit(1);
        }

        for (String emailAddress : emailAddresses) {
            logger.info("Processing email addresses {}", StringUtils.join(emailAddresses, ", "));
        }
    }


    // PROTECTED METHODS


    protected OptionSet parseOptions(String[] args) {

        OptionSet    options;

        parser.allowsUnrecognizedOptions();
        parser.acceptsAll(Arrays.asList(OPT_HELP), "Display help/usage information").forHelp();
        parser.nonOptions("One or more email addresses, separated by whitespace");

        options = parser.parse(args);
        emailAddresses = (List<String>) options.nonOptionArguments();
        helpRequested = (options.has("h") || options.has("help"));

        return options;
    }
}