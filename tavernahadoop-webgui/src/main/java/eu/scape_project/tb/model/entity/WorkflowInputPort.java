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
import java.util.Date;

import javax.persistence.*;

/**
 * Workflow input port entity.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@Entity
@Table(name = "workflowinputport")
public class WorkflowInputPort implements Serializable {

    private long wfpid;
    private String portname;
    private String defaultvalue;

    /**
     * Empty constructor.
     */
    public WorkflowInputPort() {
    }

    /**
     * Auto-generated id of the Workflow Input Port entity.
     * @return Workflow Input Port id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wfpid")
    public long getWfpid() {
        return wfpid;
    }

    /**
     * Setter for the Workflow Input Port id
     * @param wfid Workflow Input Port id
     */
    public void setWfpid(long wfpid) {
        this.wfpid = wfpid;
    }

    @Column(name = "portname")
    public String getPortname() {
        return portname;
    }

    public void setPortname(String portname) {
        this.portname = portname;
    }

    @Column(name = "defaultvalue")
    public String getDefaultvalue() {
        return defaultvalue;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }
}
