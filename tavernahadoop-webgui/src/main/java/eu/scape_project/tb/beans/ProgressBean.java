/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.tb.beans;

import eu.scape_project.tb.model.dao.WorkflowRunDao;
import eu.scape_project.tb.model.entity.WorkflowRun;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
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
    
    private List jobs;
    private HashMap jobsProgress;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String index = externalContext.getRequestParameterMap().get("wfrid");
        WorkflowRunDao wfrdao = new WorkflowRunDao();
        this.selectedWorkflowRun = wfrdao.findByWorkflowRunIdentifier(index);
        
        jobs = new ArrayList<String>();
        jobs.add("one");
        jobs.add("two");
        jobsProgress = new HashMap<String,Integer>();
        jobsProgress.put("one",50);
        jobsProgress.put("two",70);
    }

    public List getJobs() {
        return jobs;
    }

    public void setJobs(List jobs) {
        this.jobs = jobs;
    }

    public HashMap getJobsProgress() {
        return jobsProgress;
    }

    public void setJobsProgress(HashMap jobsProgress) {
        this.jobsProgress = jobsProgress;
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
