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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * Created by mrelac on 12/06/2017.
 */
@Configuration
@EnableWebSecurity
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()

                .antMatchers(HttpMethod.GET, "/admin/**").hasRole(ADMIN)
                .antMatchers(HttpMethod.GET, "/contacts/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/contacts/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/contacts/**").access("hasRole('USER') or hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/summary/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/register").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/unregister").access("hasRole('USER') or hasRole('ADMIN')")

                .and().httpBasic()
                .and().csrf()
                .and().exceptionHandling().accessDeniedPage("/login")

                .and()
                .formLogin().loginPage("/login")
                .defaultSuccessUrl("/summary")
                .usernameParameter("ssoId").passwordParameter("password")

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/**")
                .permitAll()
        ;
    }

    // FIXME Replace with db authentication and roles.
    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("mrelac@ebi.ac.uk").password("abc").roles(USER, ADMIN)
                .and()
                .withUser("admin").password("aaa").roles(ADMIN)
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .basicAuthorization("mrelac@ebi.ac.uk", "abc")
                .build();
    }
}