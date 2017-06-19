package org.mousephenotype.ri.ws.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("file:${user.home}/configfiles/${profile:dev}/ri.test.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${mi-admin-password}")
    String miAdmninPassword;

    public static final String ROLE_USER = "mi-user";
    public static final String ROLE_ADMIN = "mi-admin";


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
                .withUser(ROLE_USER).password(miAdmninPassword).roles(ROLE_USER)
        .and()
                .withUser(ROLE_ADMIN).password(miAdmninPassword).roles(ROLE_ADMIN)
        ;
    }
}
