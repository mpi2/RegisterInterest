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

import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.extract.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.mousephenotype.ri.core.entities.ImitsStatus;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.UrlUtils;

import java.util.*;

/**
 * Created by mrelac on 03/05/16.
 */
@Configuration
public class ExtractGeneConfigBeans {

    private Map<DownloadFileEnum, DownloadFilename> downloadFilenameMap = new HashMap<>();
    private Map<String, ImitsStatus> imitsStatusMap;        // key = status
    private Map<String, Gene> genesMap;                     // key = mgi accession id

    @NotNull
    @Value("${GeneStatusChangeUrl}")
    protected String geneStatusChangeUrl;

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
        GENE_STATUS_CHANGE
    }


    @PostConstruct
    public void initialise() {

        filenames = new DownloadFilename[]{

                new DownloadFilename(DownloadFileEnum.GENE_STATUS_CHANGE, geneStatusChangeUrl, downloadWorkspace + "/GeneStatusChange.tsv")
        };

        for (DownloadFilename downloadFilename : filenames) {

            downloadFilename.sourceUrl = UrlUtils.getRedirectedUrl(downloadFilename.sourceUrl);                         // Resolve any URL redirection.
            downloadFilenameMap.put(downloadFilename.downloadFileEnum, downloadFilename);
        }

        imitsStatusMap = sqlUtils.getImitsStatusMap();
        genesMap = sqlUtils.getGenes();
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


    @Bean(name = "geneLoader")
    public GeneLoader geneLoader() throws InterestException {
        Map<GeneLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        filenameKeys.put(GeneLoader.FilenameKeys.EBI_Gene, downloadFilenameMap.get(DownloadFileEnum.GENE_STATUS_CHANGE).targetFilename);

        return new GeneLoader(filenameKeys);
    }

    @Bean(name = "geneProcessor")
    public GeneProcessor geneProcessor() throws InterestException {
        return new GeneProcessor(imitsStatusMap, genesMap);
    }

    @Bean(name = "geneWriter")
    public GeneWriter geneWriter() {
        return new GeneWriter();
    }
}