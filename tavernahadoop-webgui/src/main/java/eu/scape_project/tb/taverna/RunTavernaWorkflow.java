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
package eu.scape_project.tb.taverna;

import eu.scape_project.tb.config.Config;
import eu.scape_project.tb.model.dao.WorkflowDao;
import eu.scape_project.tb.model.entity.Workflow;
import eu.scape_project.tb.model.entity.WorkflowRun;
import eu.scape_project.tb.taverna.rest.TavernaServerRestClient;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run taverna workflow. Uses the tavernahttp-restclient module for managing
 * requests to the Taverna Server.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class RunTavernaWorkflow {

    private static Logger logger = LoggerFactory.getLogger(RunTavernaWorkflow.class.getName());
    private static Config config;

    /**
     * Create new configuration based on the config.properties file.
     */
    static {
        config = new Config();
    }

    /**
     * Submit, initialise, and run a taverna workflow using the
     * tavernahttp-restclient module. The workflow run is initialised using the
     * workflow object and the related workflow run object. The key-value map of
     * the workflow input ports is provided additionally because it allows to
     * run the workflow with default values defined by the workflow input ports
     * objects related to the workflow or any transient values that need not to
     * be persisted.
     *
     * @param workflow Workflow object
     * @param workflowRun Workflow run object
     * @param kvMap Key value map of workflow input ports
     * @return Success/failure of the workflow run creation
     */
    public static Boolean run(Workflow workflow, WorkflowRun workflowRun, Map<String, String> kvMap) {
        Boolean success = new Boolean(false);
        String host = config.getProp("taverna.server.host");
        int port = Integer.parseInt(config.getProp("taverna.server.host"));
        String basepath = config.getProp("taverna.server.restapi.basepath");
        TavernaServerRestClient tsrc = new TavernaServerRestClient(host, port, basepath);
        tsrc.setUser(config.getProp("taverna.server.username"));
        tsrc.setPassword(config.getProp("taverna.server.password"));
        try {
            // 1. POST WORKFLOW

            File workflowFile = new File(config.getProp("taverna.workflow.upload.path") + workflow.getFilename());
            String uuidRURL = tsrc.submitWorkflow(workflowFile);
            // Set UUID resource URL
            workflowRun.setUuidBaseResourceUrl(uuidRURL);
            logger.info(workflowRun.getUuidBaseResourceUrl());

            // 2. PUT INPUT PORTS
            URL resourceUrl = new URL(workflowRun.getUuidBaseResourceUrl());
            for (String key : kvMap.keySet()) {
                tsrc.setWorkflowInput(resourceUrl, key, kvMap.get(key));
            }

            // Add workflow run to workflow
            workflow.addWorkflowRun(workflowRun);

            // Persist workflow
            WorkflowDao wfdao = new WorkflowDao();
            wfdao.update(workflow);

        } catch (org.apache.http.HttpException ex) {
            logger.error("Error", ex);
        } catch (MalformedURLException ex) {
            logger.error("Error", ex);
        } catch (IOException ex) {
            logger.error("Error", ex);
        }
        return success;
    }
}
