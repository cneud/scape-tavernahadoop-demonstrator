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
package eu.scape_project.tb.model.factory;

import eu.scape_project.tb.model.entity.Workflow;
import eu.scape_project.tb.model.entity.WorkflowInputPort;
import eu.scape_project.tb.taverna.t2flow.T2FlowFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;

/**
 * Workflow factory. Creates a workflow object based on an uploaded T2flow file.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class WorkflowFactory {

    private static Logger logger = LoggerFactory.getLogger(WorkflowFactory.class.getName());

    /**
     * Create a workflow object based on an uploaded T2flow file.
     *
     * @param t2flowAbsPath Absolute path to the T2flow file.
     * @return Workflow object
     */
    public static Workflow createWorkflow(String t2flowAbsPath) {

        Workflow wf = new Workflow();
        wf.setFilename(t2flowAbsPath.substring(t2flowAbsPath.lastIndexOf(File.separator) + 1));
        wf.setCreateddate(new Date());
        T2FlowFile t2flow = new T2FlowFile(t2flowAbsPath);
        wf.setUUIDInputPort(t2flow.hasUUIDInputPort());
        uk.org.taverna.scufl2.api.core.Workflow t2Wf = t2flow.getFirstWorkflow();
        NamedSet<InputWorkflowPort> t2InputPorts = t2Wf.getInputPorts();
        ArrayList<WorkflowInputPort> wfInputPorts = new ArrayList<WorkflowInputPort>();
        for (InputWorkflowPort t2InputPort : t2InputPorts) {
            String t2PortName = t2InputPort.getName();
            WorkflowInputPort wfip = new WorkflowInputPort();
            wfip.setPortname(t2PortName);
            wfInputPorts.add(wfip);
            logger.info("Workflow input port added: " + t2PortName);
        }
        wf.setWorkflowInputPorts(wfInputPorts);
        return wf;
    }
}
