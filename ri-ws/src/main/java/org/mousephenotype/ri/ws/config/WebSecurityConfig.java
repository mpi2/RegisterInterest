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

package org.mousephenotype.ri.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by mrelac on 12/06/2017.
 */
@Configuration
@EnableWebSecurity
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ri_admin_password}")
    String riAdminPassword;

    public static final String ROLE_USER = "ri-user";
    public static final String ROLE_ADMIN = "ri-admin";


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/contacts/**")
                .hasRole(ROLE_ADMIN)

                .and()

                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/contacts/**")
                .hasRole(ROLE_ADMIN)

                .and()

                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE, "/contacts/**")
                .hasRole(ROLE_ADMIN)

                .and()

                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/reports/**")
                .hasRole(ROLE_ADMIN)

                .and()
                .httpBasic()

                .and()

                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/**")
                .permitAll()
        ;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(ROLE_USER).password(riAdminPassword).roles(ROLE_USER)
        .and()
                .withUser(ROLE_ADMIN).password(riAdminPassword).roles(ROLE_ADMIN)
        ;
    }
}
