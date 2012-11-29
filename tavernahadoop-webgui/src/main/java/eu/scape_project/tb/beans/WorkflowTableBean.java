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

import eu.scape_project.tb.model.dao.WorkflowDao;
import eu.scape_project.tb.model.entity.Workflow;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;

/**
 * Backing bean of the workflow table component.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@ManagedBean(name = "workflowTable")
public class WorkflowTableBean implements Serializable {

    private List<Workflow> workflows;

    /**
     * Constructor of the workflow table bean.
     */
    public WorkflowTableBean() {
        WorkflowDao eDao = new WorkflowDao();
        workflows = eDao.findWorkflows();
    }

    /**
     * Getter for the workflows list.
     * @return workflows.
     */
    public List<Workflow> getWorkflows() {
        return workflows;
    }
}
