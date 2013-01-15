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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workflow entity.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@Entity
@Table(name = "workflow")
public class Workflow implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(Workflow.class.getName());
    private long wfid;
    private List<WorkflowInputPort> workflowInputPorts = new ArrayList<WorkflowInputPort>();
    private List<WorkflowRun> workflowRuns = new ArrayList<WorkflowRun>();
    private String filename;
    private Date createddate;
    private boolean UUIDInputPort;
    
    private String description;
    
    private String myExperimentContentUri;

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "myexpconturi")
    public String getMyExperimentContentUri() {
        return myExperimentContentUri;
    }

    public void setMyExperimentContentUri(String myExperimentContentUri) {
        this.myExperimentContentUri = myExperimentContentUri;
    }

    /**
     * Empty constructor.
     */
    public Workflow() {
    }

    /**
     * Auto-generated id of the workflow entity.
     *
     * @return Workflow id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wfid")
    public long getWfid() {
        return wfid;
    }

    /**
     * Setter for the Workflow id
     *
     * @param wfid Workflow id
     */
    public void setWfid(long wfid) {
        this.wfid = wfid;
    }

    /**
     * Getter of the workflow input ports of this workflow.
     *
     * @return Workflow input ports of this workflow
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wfid")
    public List<WorkflowInputPort> getWorkflowInputPorts() {
        return this.workflowInputPorts;
    }

    /**
     * Setter of the workflow input ports of this workflow.
     *
     * @param workflowPorts workflow input ports of this workflow
     */
    public void setWorkflowInputPorts(List<WorkflowInputPort> workflowPorts) {
        this.workflowInputPorts = workflowPorts;
    }

    /**
     * Set workflow input port value by port name.
     *
     * @param portName Workflow input port name
     * @param portValue Workflow input port value
     */
    public void setWorkflowInputPort(String portName, String portValue) {
        Iterator it = workflowInputPorts.iterator();
        while (it.hasNext()) {
            WorkflowInputPort wip = (WorkflowInputPort) it.next();
            if (wip.getPortname().equals(portName)) {
                wip.setDefaultvalue(portValue);
                break;
            }
        }
    }

    /**
     * Getter of the workflow runs related to this workflow.
     *
     * @return Workflow runs related to this workflow
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wfid")
    public List<WorkflowRun> getWorkflowRuns() {
        return workflowRuns;
    }

    /**
     * Setter of the workflow runs related to this workflow.
     *
     * @param workflowRuns Workflow runs related to this workflow
     */
    public void setWorkflowRuns(List<WorkflowRun> workflowRuns) {
        this.workflowRuns = workflowRuns;
    }

    /**
     * Add a new workflow runs related to this workflow.
     *
     * @param workflowRun Workflow run related to this workflow
     */
    public void addWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRuns.add(workflowRun);
    }

    /**
     * Getter of the T2flow filename of the workflow (without path)
     *
     * @return T2flow filename of the workflow (without path)
     */
    @Column(name = "filename")
    public String getFilename() {
        return filename;
    }

    /**
     * Setter of the T2flow filename of the workflow (without path)
     *
     * @param filename T2flow filename of the workflow (without path)
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Getter of the date when the workflow has been created.
     *
     * @return Date when the workflow has been created
     */
    @Column(name = "createddate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreateddate() {
        return createddate;
    }

    /**
     * Setter of the date when the workflow has been created.
     *
     * @param createddate Date when the workflow has been created.
     */
    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    /**
     * Getter of the UUIDInputPort field.
     *
     * @return True if the workflow has an uuid input port, false otherwise
     */
    @Column(name = "uuidinputport")
    public boolean isUUIDInputPort() {
        return UUIDInputPort;
    }

    /**
     * Setter of the UUIDInputPort field.
     *
     * @param UUIDInputPort True if the workflow has an uuid input port, false
     * otherwise
     */
    public void setUUIDInputPort(boolean UUIDInputPort) {
        this.UUIDInputPort = UUIDInputPort;
    }
}
