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
package eu.scape_project.tb.model.entity;

import eu.scape_project.tb.taverna.WebAppTavernaRestClient;
import eu.scape_project.tb.taverna.rest.TavernaRestUtil;
import eu.scape_project.tb.taverna.rest.TavernaWorkflowStatus;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workflow run entity.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@Entity
@Table(name = "workflowrun")
public class WorkflowRun implements Serializable {

    private long wfrid;
    private static Logger logger = LoggerFactory.getLogger(WorkflowRun.class.getName());
    private String uuidBaseResourceUrl;
    private String uuid;
    private Date createddate;
    private TavernaWorkflowStatus runstatus;

    /**
     * Empty constructor.
     */
    public WorkflowRun() {
    }

    /**
     * Auto-generated id of the Workflow Run entity.
     *
     * @return Workflow Run id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wfrid")
    public long getWfrid() {
        return wfrid;
    }

    /**
     * Setter for the Workflow Run id
     *
     * @param wfid Workflow Run id
     */
    public void setWfrid(long wfrid) {
        this.wfrid = wfrid;
    }

    public String getUuidBaseResourceUrl() {
        return uuidBaseResourceUrl;
    }

    public void setUuidBaseResourceUrl(String uuidBaseResourceUrl) {
        this.uuid = TavernaRestUtil.getUUIDfromUUIDResourceURL(uuidBaseResourceUrl);
        this.uuidBaseResourceUrl = uuidBaseResourceUrl;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(name = "createddate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    /**
     * Run workflow with default values, workflow default port values are used.
     *
     * @param workflow Workflow.
     */
//    public void run(Workflow workflow, Map<String, String> kvMap) {
//        FacesContext context = FacesContext.getCurrentInstance();
//        WebAppTavernaRestClient tavernaRestClient = (WebAppTavernaRestClient) context.getApplication().evaluateExpressionGet(context, "#{tavernarestclient}", WebAppTavernaRestClient.class);
//        tavernaRestClient.run(workflow, this, kvMap);
//    }

    /**
     * Run workflow with default values, workflow default port values are used.
     *
     * @param workflow Workflow.
     */
//    public void run(Workflow workflow) {
//        Map<String, String> kvMap = new HashMap<String, String>();
//        for (WorkflowInputPort wfip : workflow.getWorkflowInputPorts()) {
//            kvMap.put(wfip.getPortname(), wfip.getDefaultvalue());
//        }
//        this.run(workflow, kvMap);
//    }

    @Column(name = "runstatus")
    public TavernaWorkflowStatus getRunstatus() {
        return runstatus;
    }

    public void setRunstatus(TavernaWorkflowStatus runstatus) {
        this.runstatus = runstatus;
    }

    public void updateRunstatus() {
        WebAppTavernaRestClient tavernaRestClient = WebAppTavernaRestClient.getInstance();
        URL url = null;
        try {
            url = new URL(uuidBaseResourceUrl);
            this.runstatus = tavernaRestClient.getRunStatus(uuidBaseResourceUrl);
        } catch (MalformedURLException ex) {
            logger.error("Invalid resource URL");
        }
    }
}
