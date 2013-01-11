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
import eu.scape_project.tb.taverna.rest.TavernaClientException;
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

    /**
     * Setting UUID resource URL implicitely sets UUID.
     *
     * @param uuidBaseResourceUrl UUID resource URL
     */
    public void setUuidBaseResourceUrl(String uuidBaseResourceUrl) {
        this.uuid = TavernaRestUtil.getUUIDfromUUIDResourceURL(uuidBaseResourceUrl);
        this.uuidBaseResourceUrl = uuidBaseResourceUrl;
    }

    /**
     * Getter for the UUID field.
     *
     * @return UUID (unique identifier of a workflow run)
     */
    public String getUuid() {
        return uuid;
    }

    /**
     *
     * Setter for the UUID field.
     *
     * @param uuid UUID (unique identifier of a workflow run)
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Getter for the createddate field.
     *
     * @return Created date
     */
    @Column(name = "createddate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreateddate() {
        return createddate;
    }

    /**
     * Setter for the createddate field.
     *
     * @param createddate Created date
     */
    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    @Column(name = "runstatus")
    public TavernaWorkflowStatus getRunstatus() {
        return runstatus;
    }

    public void setRunstatus(TavernaWorkflowStatus runstatus) {
        this.runstatus = runstatus;
    }
    
    public void updateRunstatus() throws TavernaClientException {
        WebAppTavernaRestClient tavernaRestClient = WebAppTavernaRestClient.getInstance();
        URL url = null;
        try {
            if(uuidBaseResourceUrl != null) {
            url = new URL(uuidBaseResourceUrl);
            this.runstatus = tavernaRestClient.getRunStatus(uuidBaseResourceUrl);
            } else {
                this.runstatus = TavernaWorkflowStatus.POLLING;
            }
        } catch (MalformedURLException ex) {
            logger.error("Invalid resource URL");
        }
    }
    
    public String viewWorkflowRun() {
        if(this.runstatus.equals(TavernaWorkflowStatus.FINISHED))
            return "finished";
        else
            return "progress";
    }
    
}
