package org.mousephenotype.ri.ws.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by mrelac on 12/06/2017.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${ws-reader}")
    String wsReader;

    @Value("${ws-reader-password}")
    String wsReaderPassword;

    @Value("${ws-admin}")
    String wsAdmin;

    @Value("${ws-admin-password}")
    String wsAdmninPassword;

    public static final String ROLE_READER = "READER";
    public static final String ROLE_ADMIN = "ADMIN";


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
                .withUser(wsReader).password(wsReaderPassword).roles(ROLE_READER)
        .and()
                .withUser(wsAdmin).password(wsAdmninPassword).roles(ROLE_ADMIN)
        ;
    }
}