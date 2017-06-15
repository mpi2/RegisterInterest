package org.mousephenotype.ri.core.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mrelac on 13/04/2017
 */
public class Interest {

    private Contact contact;
    private List<Gene> genes = new ArrayList<>();

    public Interest() {

    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }
}