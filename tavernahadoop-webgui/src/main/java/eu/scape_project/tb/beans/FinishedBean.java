/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.scape_project.tb.beans;

import eu.scape_project.tb.model.dao.WorkflowRunDao;
import eu.scape_project.tb.taverna.WebAppTavernaRestClient;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@ManagedBean(name = "finished")
@SessionScoped
public class FinishedBean extends ProgressBean implements Serializable {
    
    String outcome;

    private static Logger logger = LoggerFactory.getLogger(FinishedBean.class.getName());
    
    @PostConstruct
    public void initFinishedBean() {
        WebAppTavernaRestClient tavernaRestClient = WebAppTavernaRestClient.getInstance();
        // TODO: Under construction
        outcome = "test";
    }
    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
