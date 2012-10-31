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

import java.io.File;
import java.io.IOException;
import java.util.*;
import eu.scape_project.tb.model.entity.WorkflowInputPort;
import org.slf4j.LoggerFactory;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

/**
 * T2Flow file. Basic access to a Taverna T2Flow workflow file using the Taverna
 * Scufl2 API.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class T2FlowFile {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(T2FlowFile.class.getName());
    private String t2flowFileName;
    private Workflow workflow;

    /**
     * Constructor using T2flow file
     *
     * @param t2flowFileName T2flow file
     */
    public T2FlowFile(String t2flowFileName) {
        this.t2flowFileName = t2flowFileName;
    }

    /**
     * Empty constructor.
     */
    private T2FlowFile() {
    }

    /**
     * First workflow's input ports.
     *
     * @return First workflow's input ports
     */
    public Set<InputWorkflowPort> getFirstWorkflowInputPorts() {
        if (this.workflow == null) {
            this.workflow = getFirstWorkflow();
        }

        return this.workflow.getInputPorts();
    }

    /**
     * First workflow's output ports.
     *
     * @return First workflow's output ports
     */
    public Set<OutputWorkflowPort> getFirstWorkflowOutputPorts() {
        if (this.workflow == null) {
            this.workflow = getFirstWorkflow();
        }
        return this.workflow.getOutputPorts();
    }

    /**
     * Get first workflow.
     *
     * @return First workflow
     */
    public Workflow getFirstWorkflow() {
        Workflow w = null;
        try {
            WorkflowBundleIO io = new WorkflowBundleIO();
            // "/home/onbscs/test.t2flow"
            File t2File = new File(t2flowFileName);
            WorkflowBundle wfBundle = io.readBundle(t2File, "application/vnd.taverna.t2flow+xml");
            NamedSet<Workflow> wfs = wfBundle.getWorkflows();
            w = wfs.first();
        } catch (ReaderException ex) {
            logger.error("T2flow reader error", ex);
        } catch (IOException ex) {
            logger.error("I/O error", ex);
        }
        return w;
    }
}
