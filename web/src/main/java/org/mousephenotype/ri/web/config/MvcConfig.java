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

package org.mousephenotype.ri.web.config;

import org.mousephenotype.ri.util.DeploymentInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mrelac on 12/06/2017.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    DeploymentInterceptor deploymentInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        logger.info("Adding WebMvc resources");

        registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/");
        registry.addResourceHandler("/docs/**").addResourceLocations("/documents/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("/resources/fonts/");
        registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/");

        super.addResourceHandlers(registry);
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver =
                new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }


    @NotNull
    @Value("${interest_base_url}")
    private String baseUrl;

    @NotNull
    @Value("${riBaseUrl}")
    String riBaseUrl;

    @NotNull
    @Value("${paBaseUrl}")
    String paBaseUrl;

    @NotNull
    @Value("${drupal_base_url}")
    private String drupalBaseUrl;


    @Bean
    public String paBaseUrl() {
        return paBaseUrl;
    }

    @Bean
    public String riBaseUrl() {
        return riBaseUrl;
    }

    @Bean
    public String drupalBaseUrl() {
        return drupalBaseUrl;
    }

    @Bean(name = "globalConfiguration")
    public Map<String, String> getGlobalConfig() {
        Map<String, String> map = new HashMap<>();
        map.put("baseUrl", baseUrl);
        map.put("drupalBaseUrl", drupalBaseUrl);
        map.put("riBaseUrl", riBaseUrl);
        map.put("paBaseUrl", paBaseUrl);
        return map;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deploymentInterceptor);
        super.addInterceptors(registry);
    }
}