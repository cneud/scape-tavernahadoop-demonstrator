/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.tb.beans;

import eu.scape_project.tb.hadoop.WebAppHadoopJobTrackerClient;
import eu.scape_project.tb.hadoopjobtracker.HDJobStatus;
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

    String wfRunId;
    String wfRunUuid;
    String wfFileName;
    WorkflowRun selectedWorkflowRun;
    private static Logger logger = LoggerFactory.getLogger(ProgressBean.class.getName());
    private List<HDJobStatus> hdJobs;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String index = externalContext.getRequestParameterMap().get("wfrid");
        WorkflowRunDao wfrdao = new WorkflowRunDao();
        this.selectedWorkflowRun = wfrdao.findByWorkflowRunIdentifier(index);
    }

    public String getWfRunId() {
        return wfRunId;
    }

    public void setWfRunId(String wfRunId) {
        this.wfRunId = wfRunId;
    }

    public String getWfFileName() {
        return wfFileName;
    }

    public void setWfFileName(String wfFileName) {
        this.wfFileName = wfFileName;
    }

    public String getWfRunUuid() {
        return wfRunUuid;
    }

    public void setWfRunUuid(String wfRunUuid) {
        this.wfRunUuid = wfRunUuid;
    }

    public List<HDJobStatus> getHdJobs() {
        return hdJobs;
    }

    public void setHdJobs(List<HDJobStatus> hdJobs) {
        this.hdJobs = hdJobs;
    }

    public void updateHdJobs() {
        this.hdJobs = WebAppHadoopJobTrackerClient.getInstance().getHadoopJobs(this.wfRunUuid);
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
}