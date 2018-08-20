package org.mousephenotype.ri.web.controller;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.entities.RIGrantedAuthority;
import org.mousephenotype.ri.core.entities.RIRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mrelac on 16/08/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
//@WebAppConfiguration
@SpringBootTest
public class InterestControllerTest  {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private InterestController interestController;

    private MockMvc mockMvc;


    @Before
    public void setUp() {

//        this.mockMvc = MockMvcBuilders
//                .webAppContextSetup(this.context)
//                .apply(springSecurity())
//                .build();
//
//        MockMvcWebClientBuilder
//                .mockMvcSetup(mockMvc)
////                .contextPath("")
////                .useMockMvcForHosts("example.com", "example.org")
//                .build()
//                ;
    }

    @After
    public void tearDown() {
    }

@Ignore
    @Test
    @WithMockUser
    public void apiSummaryUnauthenticatedUser() throws Exception {

        String url = "/api/summary";

        this.mockMvc.perform(
                get(url)
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                    .andExpect(status().is4xxClientError())
        ;
    }


    @Test
    @PreAuthorize("authenticated")
//    @WithMockCustomUser
    public void apiSummaryAuthenticatedUser() throws Exception {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String url = "/api/summary";

        this.mockMvc.perform(
                get(url)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.comparesEqualTo(0)))
        ;
    }

//    @Test
//    public void apiSummaryList() {
//    }
//
//    @Test
//    public void apiRegistrationGeneInfo() {
//    }
//
//    @Test
//    public void apiRegistrationGene() {
//    }
//
//    @Test
//    public void apiRoles() {
//    }
//
//    @Test
//    public void apiUnregistrationGene() {
//    }


//    final class WithUserDetailsSecurityContextFactory
//            implements WithSecurityContextFactory<WithUserDetails> {
//
//        private UserDetailsService userDetailsService;
//
//        @Autowired
//        public WithUserDetailsSecurityContextFactory(UserDetailsService userDetailsService) {
//            this.userDetailsService = userDetailsService;
//        }
//
//        public SecurityContext createSecurityContext(WithUserDetails withUser) {
//            String username = withUser.value();
//            Assert.hasLength(username, "value() must be non-empty String");
//            UserDetails     principal      = userDetailsService.loadUserByUsername(username);
//            Authentication  authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
//            SecurityContext context        = SecurityContextHolder.createEmptyContext();
//            context.setAuthentication(authentication);
//            return context;
//        }
//    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
    public @interface WithMockCustomUser {

        String username() default "user1@ebi.ac.uk";

        String password() default "user1pass";
    }

    public class WithMockCustomUserSecurityContextFactory
            implements WithSecurityContextFactory<WithMockCustomUser> {
        @Override
        public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            CustomUserDetails principal =
                    new CustomUserDetails(customUser.username(), customUser.password());
            Authentication auth =
                    new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
            context.setAuthentication(auth);
            return context;
        }
    }

    public class CustomUserDetails implements UserDetails {

        private String username;
        private String password;

        public CustomUserDetails() {
            username = "user1@ebi.ac.uk";
            password = "user1pass";
        }

        public CustomUserDetails(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            ArrayList<GrantedAuthority> auths = new ArrayList<>();
            auths.add(new RIGrantedAuthority(RIRole.USER));
            return auths;
        }

        @Override
        public String getPassword() {
            return username;
        }

        @Override
        public String getUsername() {
            return password;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }


}