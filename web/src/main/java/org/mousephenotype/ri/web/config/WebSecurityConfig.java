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

import org.mousephenotype.ri.core.utils.UrlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mrelac on 12/06/2017.
 *
 * Design of sample login screen taken from http://websystique.com/spring-security/spring-security-4-hibernate-annotation-example/
 */
@Configuration
@EnableWebSecurity
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource riDataSource;
    private String     paBaseUrl;
    private String     riBaseUrl;

    @Inject
    public WebSecurityConfig(String paBaseUrl, String riBaseUrl, DataSource riDataSource) {
        this.paBaseUrl = paBaseUrl;
        this.riBaseUrl = riBaseUrl;
        this.riDataSource = riDataSource;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()

                // Web service
                .antMatchers(HttpMethod.GET, "/api/admin/**").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/admin/**").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/admin/**").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/summary/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/api/registration/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/registration/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/unregistration/**").access("hasRole('USER') or hasRole('ADMIN')")

                // Web pages
                .antMatchers(HttpMethod.GET, "/summary").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/registration/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/unregistration/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/account").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/account").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET,"/**")
                .permitAll()

                .and()
                .exceptionHandling()
                .accessDeniedPage("/Access_Denied")

                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error")
                .defaultSuccessUrl("/summary")
                .successHandler(new RiSavedRequestAwareAuthenticationSuccessHandler())
                .usernameParameter("ssoId")
                .passwordParameter("password")

                .and()
//                        .csrf().ignoringAntMatchers("/api/**")
                .csrf().disable()
        ;
    }

    @Autowired
    public void configureGlobalSecurityJdbc(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(bcryptPasswordEncoder())

                .and()

                .jdbcAuthentication()
                .dataSource(riDataSource)
                .rolePrefix("ROLE_")
                .usersByUsernameQuery("SELECT address AS username, password, 'true' AS enabled FROM contact WHERE address = ?")
                .authoritiesByUsernameQuery("SELECT c.address AS username, cr.role FROM contact c JOIN contact_role cr ON cr.contact_pk = c.pk WHERE c.address = ?")
        ;
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // FIXME - Refactor using SavedRequestAwareAuthenticationSuccessHandler.
    public class RiSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

        private RequestCache requestCache = new HttpSessionRequestCache();

        public RiSavedRequestAwareAuthenticationSuccessHandler() {
        }

        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

            HttpSession  session            = request.getSession(true);
            SavedRequest savedRequest       = this.requestCache.getRequest(request, response);
            String       targetUrlParameter = this.getTargetUrlParameter();

            if (savedRequest != null) {
                String targetUrl = savedRequest.getRedirectUrl();
                logger.info("targetUrl {}", targetUrl);
            }
            logger.info("targetUrlParameter = {}", targetUrlParameter);





            String target = (String) request.getSession().getAttribute("target");

            String target1 = target = (String) session.getAttribute("target");






            logger.info("session attribute 'target' = {}", target);
            logger.info("session attribute 'target1' = {}", target1);

            String riToken = request.getRequestedSessionId();
            request.getSession().setAttribute("riToken", riToken);
            logger.info("riToken = {}", riToken);

            String referer = request.getHeader("referer");
            if (referer == null)
                logger.info("referer is NULL!");
            else
                logger.info("referer = {}", referer);
            Map<String, String> params = UrlUtils.getParams(referer);
            target = params.get("target");

            if (target != null) {

                StringBuilder paSuccessHandlerTarget = new StringBuilder()
                        .append(paBaseUrl).append("/riSuccessHandler")
                        .append("?target=" + target)
                        .append("&riToken=" + riToken);

                clearAuthenticationAttributes(request);
                logger.info("paSuccessHandlerTarget: {}", paSuccessHandlerTarget);
                getRedirectStrategy().sendRedirect(request, response, paSuccessHandlerTarget.toString());

                // FIXME - I don't think we need this any more. Remove the code that inserts it into the session.
                // Remove target from the session attributes.
                request.getSession().removeAttribute("target");

            } else {

                super.onAuthenticationSuccess(request, response, authentication);
            }
        }

        public void setRequestCache(RequestCache requestCache) {
            this.requestCache = requestCache;
        }
    }
}