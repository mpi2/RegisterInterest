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
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

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

    public class RiSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

        public RiSavedRequestAwareAuthenticationSuccessHandler() {
            super();
        }

        private RequestCache requestCache = new HttpSessionRequestCache();

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response, Authentication authentication)
                throws ServletException, IOException
        {

            logger.info("onAuthenticationSuccess!");

            String paBaseUrlWithScheme = UrlUtils.urlWithScheme(request, paBaseUrl);
            String riBaseUrlWithScheme = UrlUtils.urlWithScheme(request, riBaseUrl);



            SavedRequest savedRequest = requestCache.getRequest(request, response);

            if (savedRequest == null) {


                String target = (String) request.getSession().getAttribute("target");
                String targetWithScheme = (target == null ? target : UrlUtils.urlWithScheme(request, target));

logger.info("savedRequest IS NULL. targetWithScheme = {}", targetWithScheme);



                if ((targetWithScheme != null) && (targetWithScheme.startsWith(paBaseUrlWithScheme))) {


                    String riToken = request.getRequestedSessionId();


logger.info("riToken = {}" , riToken);

                    StringBuilder paSuccessHandlerTarget = new StringBuilder()
                            .append(paBaseUrlWithScheme).append("/riSuccessHandler")
                            .append("?target=" + targetWithScheme)
                            .append("&riToken=" + riToken);


                    clearAuthenticationAttributes(request);
logger.info("target: {}", targetWithScheme);
                    getRedirectStrategy().sendRedirect(request, response, paSuccessHandlerTarget.toString());

                    // Remove target from the session attributes.
                    request.getSession().removeAttribute("target");
                }

                clearAuthenticationAttributes(request);

                String targetUrl = riBaseUrlWithScheme + "/summary";


logger.info("targetUrl = {}", targetUrl);




                getRedirectStrategy().sendRedirect(request, response, targetUrl);
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }

            String targetUrlParameter = getTargetUrlParameter();
            if (isAlwaysUseDefaultTargetUrl()
                    || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {


            logger.info("isAlwaysUseDefaultTargetUrl()");



            requestCache.removeRequest(request, response);
                super.onAuthenticationSuccess(request, response, authentication);

                return;
            }

            clearAuthenticationAttributes(request);

            // Use the DefaultSavedRequest URL
            String targetUrl = savedRequest.getRedirectUrl();
logger.info("Redirecting to DefaultSavedRequest Url: " + targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}