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

package org.mousephenotype.ri.extract;

import org.mousephenotype.ri.extract.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.mousephenotype.ri.core.entities.GeneStatusChange;
import org.mousephenotype.ri.core.entities.ImitsStatus;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.UrlUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 03/05/16.
 */
@Configuration
@Import(AppConfig.class)
public class ExtractGeneStatusChangeConfigBeans {


    private Map<String, GeneStatusChange> geneStatusChangeMap = new HashMap<>();    // key = marker accession id

    private Map<DownloadFileEnum, DownloadFilename> downloadFilenameMap = new HashMap<>();
    private Map<String, ImitsStatus> imitsStatusMap = new HashMap<>();

    @NotNull
    @Value("${download.workspace}")
    protected String downloadWorkspace;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Autowired
    private SqlUtils sqlUtils;


    public class DownloadFilename {
        public final DownloadFileEnum downloadFileEnum;
        public String sourceUrl;
        public final String targetFilename;

        public DownloadFilename(DownloadFileEnum downloadFileEnum, String sourceUrl, String targetFilename) {
            this.downloadFileEnum = downloadFileEnum;
            this.sourceUrl = sourceUrl;
            this.targetFilename = targetFilename;
        }
    }


    public DownloadFilename[] filenames;
    private enum DownloadFileEnum {
        EBI_GeneStatusChange
    }


    @PostConstruct
    public void initialise() {

        filenames = new DownloadFilename[]{

                new DownloadFilename(DownloadFileEnum.EBI_GeneStatusChange, "http://i-dcc.org/imits/v2/reports/mp2_load_phenotyping_colonies_report.tsv", downloadWorkspace + "/EBI_GeneStatusChange.csv")
        };

        for (DownloadFilename downloadFilename : filenames) {

            downloadFilename.sourceUrl = UrlUtils.getRedirectedUrl(downloadFilename.sourceUrl);                         // Resolve any URL redirection.
            downloadFilenameMap.put(downloadFilename.downloadFileEnum, downloadFilename);
        }

        imitsStatusMap = sqlUtils.getImitsStatusMap();
    }

    @Bean
    public List<Downloader> downloader() {
        List<Downloader> downloaderList = new ArrayList<>();

        for (DownloadFilename download : filenames) {
            downloaderList.add(new Downloader(download.sourceUrl, download.targetFilename));
        }

        return downloaderList;
    }


    // LOADERS, PROCESSORS, AND WRITERS


    @Bean(name = "geneStatusChangeLoader")
    public GeneStatusChangeLoader geneStatusChangeLoader() throws InterestException {
        Map<GeneStatusChangeLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        filenameKeys.put(GeneStatusChangeLoader.FilenameKeys.EBI_GeneStatusChange, downloadFilenameMap.get(DownloadFileEnum.EBI_GeneStatusChange).targetFilename);

        return new GeneStatusChangeLoader(filenameKeys, imitsStatusMap);
    }

    @Bean(name = "geneStatusChangeProcessor")
    public GeneStatusChangeProcessor geneStatusChangeProcessor() throws InterestException {
        return new GeneStatusChangeProcessor(imitsStatusMap);
    }

    @Bean(name = "geneStatusChangeWriter")
    public GeneStatusChangeWriter geneStatusChangeWritere() {
        return new GeneStatusChangeWriter();
    }
}