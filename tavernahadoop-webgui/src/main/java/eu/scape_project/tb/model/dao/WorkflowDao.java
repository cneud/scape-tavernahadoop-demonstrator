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
package eu.scape_project.tb.model.dao;

import eu.scape_project.tb.model.entity.Workflow;
import eu.scape_project.tb.hibernate.HibernateUtil;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Data Access Object (DAO) layer of the workflow object. Provides the means
 * for adding, updating, and accessing workflow objects.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class WorkflowDao {

    private static Logger logger = LoggerFactory.getLogger(WorkflowDao.class.getName());
    Session currentSession;

    /**
     * Constructor of the workfow Data Access Object (DAO).
     */
    public WorkflowDao() {
        currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
    }

    /**
     * Create new workflow object.
     *
     * @param workflow Workflow object
     */
    public void save(Workflow workflow) {
        currentSession.save(workflow);
    }

    /**
     * Update workflow object.
     *
     * @param workflow Workflow object
     */
    public void update(Workflow workflow) {
        currentSession.update(workflow);
    }

    /**
     * Get the list of workflows ordered by submission date.
     *
     * @return Workflow object
     */
    public List<Workflow> findWorkflows() {
        List<Workflow> list = (List<Workflow>) currentSession.createQuery("from Workflow order by createddate desc").list();
        return list;
    }

    /**
     * Get a workflow by identifier.
     *
     * @param workflowIdentifier Workflow identifier
     * @return Workflow object
     */
    public Workflow findByWorkflowIdentifier(String workflowIdentifier) {
        List list = currentSession.createQuery("from Workflow where wfid=" + workflowIdentifier).list();
        return list.isEmpty() ? null : (Workflow) list.get(0);
    }
}
