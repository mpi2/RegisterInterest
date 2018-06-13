/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.ri.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.ri.core.entities.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class SummaryController {

    @Autowired
    RestTemplate restTemplate;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @RequestMapping(value = {"/summary" }, method = RequestMethod.GET)
    public String homePage(HttpServletRequest request, ModelMap model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        model.addAttribute("user", getPrincipal());
        model.addAttribute("uri", request.getRequestURI());
        model.addAttribute("roles", auth.getAuthorities());
        model.addAllAttributes(config);

        System.out.println("user/principal = " + getPrincipal());
        System.out.println("uri = " + request.getRequestURI());
        System.out.println("url = " + request.getRequestURL());
        System.out.println("roles = " + StringUtils.join(auth.getAuthorities(), ", "));



        Summary[]  summary = restTemplate.getForObject(
                "https://www.ebi.ac.uk/mi/impc/interest/contacts?email=mrelac@ebi.ac.uk", Summary[].class
        );


        List<Summary.Gene> genes = summary[0].getGenes();

        genes.sort(Comparator.comparing(Summary.Gene::getSymbol));


        model.addAttribute(summary);

        return "summary";
    }

    @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
    public String accessDeniedPage(ModelMap model) {
        model.addAttribute("user", getPrincipal());
        return "accessDenied";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(HttpServletRequest request) {

        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/summary";
    }

    private String getPrincipal(){
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }
}