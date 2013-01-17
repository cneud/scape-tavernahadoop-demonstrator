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

import eu.scape_project.tb.config.TavernaConfig;
import eu.scape_project.tb.model.dao.WorkflowDao;
import eu.scape_project.tb.model.entity.Workflow;
import eu.scape_project.tb.model.entity.WorkflowInputPort;
import eu.scape_project.tb.model.entity.WorkflowRun;
import eu.scape_project.tb.model.factory.WorkflowFactory;
import eu.scape_project.tb.rest.DefaultHttpClientException;
import eu.scape_project.tb.rest.util.FileUtility;
import eu.scape_project.tb.taverna.MyExperimentRestClient;
import eu.scape_project.tb.taverna.WebAppTavernaRestClient;
import eu.scape_project.tb.taverna.rest.TavernaClientException;
import eu.scape_project.tb.taverna.rest.TavernaWorkflowStatus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing bean of the overview page.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 *
 * TODO: Workflow run table is not updated when the run is started
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
     * Listener to set the current workflow.
     */
    public void deleteWorkflow() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String wfid = externalContext.getRequestParameterMap().get("wfid");
        try {
            long id = Long.parseLong(wfid);
            WorkflowDao wfdao = new WorkflowDao();
            wfdao.deleteByWorkflowIdentifier(id);
        } catch (NumberFormatException ex) {
            logger.error("Error parsing workflow id number");
        }
    }

    private void uploadFile(String fileName, InputStream is) {
        String wfDirParam = (new TavernaConfig()).getProp("taverna.workflow.upload.path");
        String wfPath = FileUtility.makePath(wfDirParam + fileName);
        File wfDir = new File(wfDirParam);
        if (!wfDir.exists()) {
            FacesMessage msgMissingUpldPath = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Workflow upload failed", "Workflow upload directory " + wfDirParam + " does not exist.");
            FacesContext.getCurrentInstance().addMessage("Workflow creation", msgMissingUpldPath);
            return;
        }
        if (!wfDir.canWrite()) {
            FacesMessage msgMissingUpldPath = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Workflow upload failed", "No permission to write to upload directory " + wfDirParam + ".");
            FacesContext.getCurrentInstance().addMessage("Workflow creation", msgMissingUpldPath);
            return;
        }
        logger.info("Copying file to: " + wfPath);
        try {
            FileOutputStream fos = new FileOutputStream(wfPath);
            IOUtils.copyLarge(is, fos);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Workflow creation", "Workflow file " + fileName + " uploaded successfully.");
            FacesContext.getCurrentInstance().addMessage("Workflow created successfully", msg);
        } catch (Exception ex) {
            logger.error("Workflow file creation error.");
            FacesMessage msgMissingUpldPath = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Workflow upload failed", ex.getMessage());
            FacesContext.getCurrentInstance().addMessage("Workflow creation", msgMissingUpldPath);
        }
    }

    private void saveWorkflow(Workflow wf) {
        // Issue warning if uuid input port is not available
        if (!wf.isUUIDInputPort()) {
            FacesMessage msgWarnUUID = new FacesMessage(FacesMessage.SEVERITY_WARN, "UUID Input port missing", "It will not be possible to show the progress of any related hadoop/pig/hive jobs");
            FacesContext.getCurrentInstance().addMessage("UUID Input port", msgWarnUUID);
        }
        selectedWorkfow = wf;
        WorkflowDao wfdao = new WorkflowDao();
        wfdao.save(wf);
    }

    /**
     * Listener to handle the file upload.
     *
     * @param event File upload event
     */
    public void handleFileUpload(FileUploadEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        UploadedFile f = event.getFile();
        String fileName = f.getFileName();
        String wfDirParam = (new TavernaConfig()).getProp("taverna.workflow.upload.path");
        String wfPath = FileUtility.makePath(wfDirParam + fileName);
        Workflow wf = WorkflowFactory.createWorkflow(wfPath);
        try {
            uploadFile(fileName, f.getInputstream());
            saveWorkflow(wf);
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to upload workflow", ex.getMessage()));
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
            if (val != null && !val.isEmpty() && !val.equals("")) {
                isValueProvided = true;
            }
            this.selectedWorkfow.setWorkflowInputPort(key, val);
        }
        if (!isValueProvided) {
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
        WebAppTavernaRestClient tavernaRestClient = WebAppTavernaRestClient.getInstance();
        Map<String, String> kvMap = new HashMap<String, String>();
        for (WorkflowInputPort wfip : this.selectedWorkfow.getWorkflowInputPorts()) {
            kvMap.put(wfip.getPortname(), wfip.getDefaultvalue());
        }
        try {
            tavernaRestClient.run(this.selectedWorkfow, wr, kvMap);
            wr.setRunstatus(TavernaWorkflowStatus.POLLING);
            wr.setCreateddate(new Date());
            this.selectedWorkfow.addWorkflowRun(wr);
            WorkflowDao wfdao = new WorkflowDao();
            wfdao.update(selectedWorkfow);
        } catch (TavernaClientException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to run workflow", ex.getMessage()));
        }
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
        Map<String, String> kvMap = getKeyValueMap();
        WebAppTavernaRestClient tavernaRestClient = WebAppTavernaRestClient.getInstance();
        try {
            tavernaRestClient.run(this.selectedWorkfow, wr, kvMap);
            wr.setRunstatus(TavernaWorkflowStatus.POLLING);
            wr.setCreateddate(new Date());
            this.selectedWorkfow.addWorkflowRun(wr);
            WorkflowDao wfdao = new WorkflowDao();
            wfdao.update(selectedWorkfow);
        } catch (TavernaClientException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to run workflow", ex.getMessage()));
        }
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
            // the prefix "wfipparam" is used to filter key value pair fields
            if (keyWithPrefix.startsWith("wfipparam")) {
                String key = keyWithPrefix.replace("wfipparam", "");
                // Do not add uuid field. It is injected after the workflow
                // is submitted to the taverna server and the uuid is known.
                if (!(key.equals("uuid"))) {
                    String val = map.get(keyWithPrefix);
                    kvMap.put(key, val);
                }
            }
        }
        return kvMap;
    }

    /**
     * Get myExperiment workflow for a given id
     *
     * @throws DefaultHttpClientException
     */
    public void getMyExperimentWorkflow() throws DefaultHttpClientException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String wfNumber = externalContext.getRequestParameterMap().get("uploadform:myexpwfid");
        logger.info("Workflow number: " + wfNumber);
        try {
            MyExperimentRestClient myExpClient = MyExperimentRestClient.getInstance();
            int wfId = Integer.parseInt(wfNumber);
            Workflow wf = myExpClient.getMyExperimentWorkflow(wfId);
            String wfDirParam = (new TavernaConfig()).getProp("taverna.workflow.upload.path");
            String wfPath = FileUtility.makePath(wfDirParam + wf.getFilename());
            InputStream wfContentStream = myExpClient.getMyExperimentWorkflowContentStream(wf);
            uploadFile(wf.getFilename(), wfContentStream);
            WorkflowFactory.completeWorkflow(wf, wfPath);
            saveWorkflow(wf);
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to get workflow from myExperiment", ex.getMessage()));
        }

    }
}