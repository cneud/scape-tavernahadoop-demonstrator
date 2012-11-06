/*
 * Copyright 2012 The SCAPE Project Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package eu.scape_project.tb.beans;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import eu.scape_project.tb.model.entity.Workflow;
import eu.scape_project.tb.model.dao.WorkflowDao;
import eu.scape_project.tb.model.factory.WorkflowFactory;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.scape_project.tb.model.entity.WorkflowRun;
import eu.scape_project.tb.taverna.rest.TavernaWorkflowStatus;

/**
 * Backing bean of the overview page.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@ManagedBean(name = "overview")
@SessionScoped
public class OverviewBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OverviewBean.class.getName());
    private Workflow selectedWorkfow;

    /**
     * Getter of the selected workflow
     *
     * @return Selected workflow
     */
    public Workflow getSelectedWorkfow() {
        return selectedWorkfow;
    }

    /**
     * Setter of the selected workflow
     *
     * @param selectedWorkfow Selected workflow
     */
    public void setSelectedWorkfow(Workflow selectedWorkfow) {
        this.selectedWorkfow = selectedWorkfow;
    }

    /**
     * Listener to set the current workflow.
     */
    public void setCurrentWorkflow() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String index = externalContext.getRequestParameterMap().get("wfid");
        WorkflowDao wfdao = new WorkflowDao();
        this.selectedWorkfow = wfdao.findByWorkflowIdentifier(index);
    }

    /**
     * Listener to handle the file upload.
     *
     * @param event File upload event
     */
    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile f = event.getFile();
        String fileName = f.getFileName();
        String absPath = "/home/onbscs/" + fileName;
        logger.info("File uploaded: " + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(absPath);
            IOUtils.copyLarge(f.getInputstream(), fos);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Workflow upload", "Workflow file " + event.getFile().getFileName() + " uploaded successfully.");
            FacesContext.getCurrentInstance().addMessage("Workflow uploaded successfully", msg);
            Workflow wf = WorkflowFactory.createWorkflow(absPath);
            selectedWorkfow = wf;
            WorkflowDao wfdao = new WorkflowDao();
            wfdao.save(wf);
        } catch (FileNotFoundException ex) {
            logger.error("File not found.");
        } catch (IOException ex) {
            logger.error("I/O Error.");
        }
    }

    /**
     * Save port values implementation.
     */
    private boolean savePortValuesImpl() {
        Map<String, String> kvMap = getKeyValueMap();
        boolean isValueProvided = false;
        if (kvMap == null || kvMap.isEmpty()) {
            return false;
        }
        for (String key : kvMap.keySet()) {
            String val = kvMap.get(key);
            if(val != null && !val.isEmpty() && !val.equals(""))
                isValueProvided = true;
            this.selectedWorkfow.setWorkflowInputPort(key, val);
        }
        if(!isValueProvided) {
            return false;
        }
        WorkflowDao wfdao = new WorkflowDao();
        wfdao.update(selectedWorkfow);
        return true;
    }

    /**
     * Save port values listener.
     */
    public void savePortValues() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!savePortValuesImpl()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Saving port values failed", "Port values need to be provided."));
            return;
        }
        context.addMessage(null, new FacesMessage("Save workflow port values", "Workflow port values saved successfully."));
    }

    /**
     * Save port values and run workflow listener.
     */
    public void saveValuesAndRunWorkflow() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!savePortValuesImpl()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to run workflow", "Port values need to be provided."));
            return;
        }
        WorkflowRun wr = new WorkflowRun();
//        wr.run(this.selectedWorkfow);
        wr.setRunstatus(TavernaWorkflowStatus.INITIALISED);
        wr.setUuidBaseResourceUrl("http://fue-l:8080/tavernaserver/rest/runs/73662556738883kkjh");
        wr.setCreateddate(new Date());
        this.selectedWorkfow.addWorkflowRun(wr);
        WorkflowDao wfdao = new WorkflowDao();
        wfdao.update(selectedWorkfow);
    }

    /**
     * Run workflow listener.
     */
    public void runWorkflow() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!savePortValuesImpl()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to run workflow", "Port values need to be provided."));
            return;
        }
        WorkflowRun wr = new WorkflowRun();
//        Map<String, String> kvMap = getKeyValueMap();
//        wr.run(this.selectedWorkfow, kvMap);
        wr.setRunstatus(TavernaWorkflowStatus.INITIALISED);
        wr.setUuidBaseResourceUrl("http://fue-l:8080/tavernaserver/rest/runs/euydks6288sksldi728");
        wr.setCreateddate(new Date());
        this.selectedWorkfow.addWorkflowRun(wr);
        WorkflowDao wfdao = new WorkflowDao();
        wfdao.update(selectedWorkfow);
    }

    /**
     * Key value map creator. Iterates over the posted form field values and
     * creates a parameter value map that is handed over to the workflow
     * processing.
     *
     * @return Key value map.
     */
    private Map<String, String> getKeyValueMap() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, String> kvMap = new HashMap<String, String>();
        Map<String, String> map = externalContext.getRequestParameterMap();
        for (String keyWithPrefix : map.keySet()) {
            if (keyWithPrefix.startsWith("wfipparam")) {
                String key = keyWithPrefix.replace("wfipparam", "");
                String val = map.get(keyWithPrefix);
                kvMap.put(key, val);
            }
        }
        return kvMap;
    }
}