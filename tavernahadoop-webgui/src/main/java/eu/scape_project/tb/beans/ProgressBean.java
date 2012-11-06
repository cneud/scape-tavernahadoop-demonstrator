/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.tb.beans;

import eu.scape_project.tb.model.dao.WorkflowDao;
import eu.scape_project.tb.model.dao.WorkflowRunDao;
import eu.scape_project.tb.model.entity.WorkflowRun;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
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
@ManagedBean(name = "progress")
@SessionScoped
public class ProgressBean implements Serializable {

    WorkflowRun selectedWorkflowRun;
    private Integer progress;
    private static Logger logger = LoggerFactory.getLogger(ProgressBean.class.getName());

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String index = externalContext.getRequestParameterMap().get("wfrid");
        WorkflowRunDao wfrdao = new WorkflowRunDao();
        this.selectedWorkflowRun = wfrdao.findByWorkflowRunIdentifier(index);
    }

    /**
     * Listener to set the current workflow.
     */
    public void setCurrentWorkflowRun() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String index = externalContext.getRequestParameterMap().get("wfrid");
        WorkflowRunDao wfrdao = new WorkflowRunDao();
        this.selectedWorkflowRun = wfrdao.findByWorkflowRunIdentifier(index);
    }

    public WorkflowRun getSelectedWorkflowRun() {
        return selectedWorkflowRun;
    }

    public void setSelectedWorkflowRun(WorkflowRun selectedWorkflowRun) {
        this.selectedWorkflowRun = selectedWorkflowRun;
    }

    public Integer getProgress() {
        logger.info("Firing getProgress");
        if (progress == null) {
            progress = 0;
        } else {
            progress = progress + (int) (Math.random() * 20);

            if (progress > 100) {
                progress = 100;
            }
        }

        return progress;
    }

    public void setProgress(Integer progress) {
        logger.info("Firing setProgress");
        this.progress = progress;
    }

    public void onComplete() {
        logger.info("Firing onComplete");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Workflow run", "Workflow run completed"));
    }

    public void cancel() {
        logger.info("Firing cancel");
        progress = null;
    }
}
