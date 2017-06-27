package org.mousephenotype.ri.ws;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by mrelac on 27/06/2017.
 */
@Controller
public class DocumentationController {

    @RequestMapping(value = {"/"})
    public String showDocsIndex(Model model) {
        return "docs/index";
    }

    @RequestMapping({"docs/{page}"})
    public String showDocs(@PathVariable("page") String pageName, Model model) {


        model.addAttribute("page", pageName);
        return "docs-template";
    }
}