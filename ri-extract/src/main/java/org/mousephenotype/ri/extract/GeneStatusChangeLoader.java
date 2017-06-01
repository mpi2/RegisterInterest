package org.mousephenotype.ri.extract;

/**
 * Created by mrelac on 22/05/2017.
 */
/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.BindException;

import org.mousephenotype.ri.core.entities.GeneStatusChange;
import org.mousephenotype.ri.core.entities.ImitsStatus;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.core.DateUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Loads the iMits Gene Status Change information from the iMits report file.
 *
 * Created by mrelac on 22/05/2017
 *
 */
public class GeneStatusChangeLoader implements InitializingBean, Step {

    private DateUtils dateUtils = new DateUtils();
    public Map<FilenameKeys, String> imitsKeys = new HashMap<>();
    private Map<String, ImitsStatus> imitsStatusMap;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private FlatFileItemReader<GeneStatusChange> geneStatusChangeReader = new FlatFileItemReader<>();

    public enum FilenameKeys {
        EBI_GeneStatusChange
    }


    // Fields within EBI_GeneStatusChange.csv:
    private final String[] geneStatusChangeColumnNames = new String[] {
              "gene_mgi_accession_id"                           // A - Gene MGI accession id
            , "gene_marker_symbol"                              // B - Gene marker symbol
            , "gene_assignment_status"                          // C - Gene assignment status
            , "gene_assigned_to"                                // D - Centre to whom gene is assigned
            , "gene_assignment_status_date"                     // E - Date gene was assigned to centre
            , "conditional_allele_production_status"            // F - Conditional allele production status
            , "conditional_allele_production_centre"            // G - Conditional allele production centre
            , "conditional_allele_status_date"                  // H - Date conditional allele status was last updated
            , "null_allele_production_status"                   // I - Null allele production status
            , "null_allele_production_centre"                   // J - Null allele production centre
            , "null_allele_status_date"                         // K - Date null allele status was last updated
            , "phenotyping_status"                              // L - Phenotyping status
            , "phenotyping_centre"                              // M - Phenotyping centre
            , "phenotyping_status_date"                         // N - Date phenotyping status was last updated
            , "number_of_significant_phenotypes"                // O - Number of significant phenotypes
    };

    @Autowired
    @Qualifier("geneStatusChangeProcessor")
    private ItemProcessor geneStatusChangeProcessor;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private GeneStatusChangeWriter writer;


    public GeneStatusChangeLoader(
            Map<FilenameKeys, String> imitsKeys,
            Map<String, ImitsStatus> imitsStatusMap

    ) throws InterestException {
        this.imitsKeys = imitsKeys;
        this.imitsStatusMap = imitsStatusMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        geneStatusChangeReader.setResource(new FileSystemResource(imitsKeys.get(FilenameKeys.EBI_GeneStatusChange)));
        geneStatusChangeReader.setComments(new String[] {"#" });
        geneStatusChangeReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<GeneStatusChange> lineMapperPhenotypedColony = new DefaultLineMapper<>();
        DelimitedLineTokenizer              tokenizerPhenotypedColony  = new DelimitedLineTokenizer(",");
        tokenizerPhenotypedColony.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerPhenotypedColony.setNames(geneStatusChangeColumnNames);
        lineMapperPhenotypedColony.setLineTokenizer(tokenizerPhenotypedColony);
        lineMapperPhenotypedColony.setFieldSetMapper(new GeneStatusChangeFieldSetMapper());
        geneStatusChangeReader.setLineMapper(lineMapperPhenotypedColony);
    }

    public class GeneStatusChangeFieldSetMapper implements FieldSetMapper<GeneStatusChange> {

        /**
         * Method used to map data obtained from a {@link FieldSet} into an object.
         *
         * @param fs the {@link FieldSet} to map
         * @throws BindException if there is a problem with the binding
         */
        @Override
        public GeneStatusChange mapFieldSet(FieldSet fs) throws BindException {
            GeneStatusChange geneStatusChange = new GeneStatusChange();

            geneStatusChange.setMgiAccessionId(fs.readString("gene_mgi_accession_id"));
            geneStatusChange.setSymbol(fs.readString("gene_marker_symbol"));
            geneStatusChange.setAssignmentStatus(fs.readString("gene_assignment_status"));
            geneStatusChange.setAssignedTo(fs.readString("gene_assigned_to"));
            geneStatusChange.setAssignmentStatusDateString(fs.readString("gene_assignment_status_date"));

            geneStatusChange.setConditionalAlleleProductionStatus(fs.readString("conditional_allele_production_status"));
            geneStatusChange.setConditionalAlleleProductionCentre(fs.readString("conditional_allele_production_centre"));
            geneStatusChange.setConditionalAlleleStatusDateString(fs.readString("conditional_allele_status_date"));

            geneStatusChange.setNullAlleleProductionStatus(fs.readString("null_allele_production_status"));
            geneStatusChange.setNullAlleleProductionCentre(fs.readString("null_allele_production_centre"));
            geneStatusChange.setNullAlleleStatusDateString(fs.readString("null_allele_status_date"));

            geneStatusChange.setPhenotypingStatus(fs.readString("phenotyping_status"));
            geneStatusChange.setPhenotypingCentre(fs.readString("phenotyping_centre"));
            geneStatusChange.setPhenotypingStatusDateString(fs.readString("phenotyping_status_date"));

            geneStatusChange.setNumberOfSignificantPhenotypesString(fs.readString("number_of_significant_phenotypes"));

            return geneStatusChange;
        }
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "phenotypedColonyLoaderStep";
    }

    /**
     * @return true if a step that is already marked as complete can be started again.
     */
    @Override
    public boolean isAllowStartIfComplete() {
        return false;
    }

    /**
     * @return the number of times a job can be started with the same identifier.
     */
    @Override
    public int getStartLimit() {
        return 1;
    }

    /**
     * Process the step and assign progress and status meta information to the {@link StepExecution} provided. The
     * {@link Step} is responsible for setting the meta information and also saving it if required by the
     * implementation.<br>
     * <p/>
     * It is not safe to re-use an instance of {@link Step} to process multiple concurrent executions.
     *
     * @param stepExecution an entity representing the step to be executed
     * @throws JobInterruptedException if the step is interrupted externally
     */
    @Override
    public void execute(StepExecution stepExecution) throws JobInterruptedException {

        Step loadGeneStatusChangeStep = stepBuilderFactory.get("loadGeneStatusChangeStep")
                .listener(new GeneStatusChangeStepListener())
                .chunk(100000)
                .reader(geneStatusChangeReader)
                .processor(geneStatusChangeProcessor)
                .writer(writer)
                .build();

        // Synchronous flows.
        FlowBuilder<Flow> synchronousFlowBuilder = new FlowBuilder<Flow>("phenotypedColonyLoaderFlow").start(loadGeneStatusChangeStep);
        Flow flow = synchronousFlowBuilder.build();

        stepBuilderFactory.get("phenotypedColonyLoaderStep")
                .flow(flow)
                .build()
                .execute(stepExecution);
    }

    public class GeneStatusChangeStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("GENE STATUS CHANGE: Added {} new geneStatusChange records to database from file {} in {}.",
                    ((GeneStatusChangeProcessor) geneStatusChangeProcessor).getGeneStatusChangeCount(),
                    ((GeneStatusChangeProcessor) geneStatusChangeProcessor).getGeneStatusChangeCount(),
                    imitsKeys.get(FilenameKeys.EBI_GeneStatusChange),
                    dateUtils.formatDateDifference(start, stop));

            logger.info("");

            return ((GeneStatusChangeProcessor) geneStatusChangeProcessor).getErrMessages();
        }
    }
}