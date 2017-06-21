package org.mousephenotype.ri.extract;

/**
 * Created by mrelac on 22/05/2017.
 */
/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
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

import org.mousephenotype.ri.core.entities.Gene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.core.DateUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Loads the iMits Gene GeneStatus Change information from the iMits report file.
 *
 * Created by mrelac on 22/05/2017
 *
 */
public class GeneLoader implements InitializingBean, Step {

    private DateUtils dateUtils = new DateUtils();
    public Map<FilenameKeys, String> imitsKeys = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private FlatFileItemReader<Gene> geneReader = new FlatFileItemReader<>();

    public enum FilenameKeys {
        EBI_Gene
    }


    // Fields within EBI_Gene.csv:
    private final String[] geneColumnNames = new String[] {
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
    private ItemProcessor geneProcessor;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private GeneWriter writer;


    public GeneLoader(Map<FilenameKeys, String> imitsKeys) throws InterestException {
        this.imitsKeys = imitsKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        geneReader.setResource(new FileSystemResource(imitsKeys.get(FilenameKeys.EBI_Gene)));
        geneReader.setComments(new String[] {"#" });
        geneReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<Gene> lineMapperPhenotypedColony = new DefaultLineMapper<>();
        DelimitedLineTokenizer              tokenizerPhenotypedColony  = new DelimitedLineTokenizer("\t");
        tokenizerPhenotypedColony.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerPhenotypedColony.setNames(geneColumnNames);
        lineMapperPhenotypedColony.setLineTokenizer(tokenizerPhenotypedColony);
        lineMapperPhenotypedColony.setFieldSetMapper(new GeneFieldSetMapper());
        geneReader.setLineMapper(lineMapperPhenotypedColony);
    }

    public class GeneFieldSetMapper implements FieldSetMapper<Gene> {

        /**
         * Method used to map data obtained from a {@link FieldSet} into an object.
         *
         * @param fs the {@link FieldSet} to map
         * @throws BindException if there is a problem with the binding
         */
        @Override
        public Gene mapFieldSet(FieldSet fs) throws BindException {
            Gene gene = new Gene();

            String s = fs.readString("gene_mgi_accession_id");
            gene.setMgiAccessionId((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("gene_marker_symbol");
            gene.setSymbol((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("gene_assigned_to");
            gene.setAssignedTo((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("gene_assignment_status");
            gene.setAssignmentStatus((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("gene_assignment_status_date");
            gene.setAssignmentStatusDateString((s != null) && ( ! s.trim().isEmpty()) ? s : null);


            s = fs.readString("conditional_allele_production_centre");
            gene.setConditionalAlleleProductionCentre((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("conditional_allele_production_status");
            gene.setConditionalAlleleProductionStatus((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("conditional_allele_status_date");
            gene.setConditionalAlleleProductionStatusDateString((s != null) && ( ! s.trim().isEmpty()) ? s : null);


            s = fs.readString("null_allele_production_centre");
            gene.setNullAlleleProductionCentre((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("null_allele_production_status");
            gene.setNullAlleleProductionStatus((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("null_allele_status_date");
            gene.setNullAlleleProductionStatusDateString((s != null) && ( ! s.trim().isEmpty()) ? s : null);


            s = fs.readString("phenotyping_centre");
            gene.setPhenotypingCentre((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("phenotyping_status");
            gene.setPhenotypingStatus((s != null) && ( ! s.trim().isEmpty()) ? s : null);

            s = fs.readString("phenotyping_status_date");
            gene.setPhenotypingStatusDateString((s != null) && ( ! s.trim().isEmpty()) ? s : null);


            s = fs.readString("number_of_significant_phenotypes");
            gene.setNumberOfSignificantPhenotypesString(s);

            return gene;
        }
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "geneLoaderStep";
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
    @Transactional
    @Override
    public void execute(StepExecution stepExecution) throws JobInterruptedException {

        Step geneLoaderStep = stepBuilderFactory.get("geneLoaderStepExecutor")
                .listener(new GeneStepListener())
                .chunk(200000)
                .reader(geneReader)
                .processor(geneProcessor)
                .writer(writer)
                .build();

        // Synchronous flows.
        FlowBuilder<Flow> synchronousFlowBuilder = new FlowBuilder<Flow>("geneLoaderFlow").start(geneLoaderStep);
        Flow flow = synchronousFlowBuilder.build();

        stepBuilderFactory.get("geneLoaderStep")
                .flow(flow)
                .build()
                .execute(stepExecution);
    }

    public class GeneStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("GENE LOADER: Added {} new gene records to database from file {} in {}.",
                    ((GeneWriter) writer).getCount(),
                    imitsKeys.get(FilenameKeys.EBI_Gene),
                    dateUtils.formatDateDifference(start, stop));

            logger.info("");

            return ((GeneProcessor) geneProcessor).getErrMessages();
        }
    }
}