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

import eu.scape_project.tb.taverna.rest.TavernaServerRestClient;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;
import eu.scape_project.tb.model.dao.WorkflowDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.scape_project.tb.taverna.RunTavernaWorkflow;

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
    private Date createddate;

    /**
     * Empty constructor.
     */
    public WorkflowRun() {
    }

    /**
     * Auto-generated id of the Workflow Run entity.
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
     * @param wfid Workflow Run id
     */
    public void setWfrid(long wfrid) {
        this.wfrid = wfrid;
    }

    public String getUuidBaseResourceUrl() {
        return uuidBaseResourceUrl;
    }

    public void setUuidBaseResourceUrl(String uuidBaseResourceUrl) {
        this.uuidBaseResourceUrl = uuidBaseResourceUrl;
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
    public void run(Workflow workflow, Map<String, String> kvMap) {
        RunTavernaWorkflow.run(workflow, this, kvMap);
    }

    /**
     * Run workflow with default values, workflow default port values are used.
     *
     * @param workflow Workflow.
     */
    public void run(Workflow workflow) {
        Map<String, String> kvMap = new HashMap<String, String>();
        for (WorkflowInputPort wfip : workflow.getWorkflowInputPorts()) {
            kvMap.put(wfip.getPortname(), wfip.getDefaultvalue());
        }
        this.run(workflow, kvMap);
    }
}
