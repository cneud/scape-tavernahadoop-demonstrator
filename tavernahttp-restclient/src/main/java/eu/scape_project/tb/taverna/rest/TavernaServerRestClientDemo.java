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
package eu.scape_project.tb.taverna.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * TavernaServerRestClientDemo class. Demonstration of the Taverna server REST
 * client.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class TavernaServerRestClientDemo {

    private static Logger logger = LoggerFactory.getLogger(TavernaServerRestClientDemo.class.getName());

    /**
     * Main method demonstrating the Taverna Server REST client.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ApplicationContext appContext =
                new ClassPathXmlApplicationContext("spring/taverna/TavernaConfig.xml");
        // Properties of the TavernaServerRestClient bean are defined in the 
        // properties file src/main/resources/tavernaconfig.properties. 
        // Host, port, base URL fields are provided at construction time, the
        // object, username and password are defined afterwards.
        TavernaServerRestClient tsrc = (TavernaServerRestClient) appContext.getBean("tavernaServerRestClient");

        try {
            // 1. POST WORKFLOW
            // The client starts by creating a workflow run. This is done by 
            // POSTing a T2flow document to the service at the address
            // http://${server}:${port}/tavernaserver/rest/runs with the content 
            // type application/vnd.taverna.t2flow+xml.
            // The result of the POST is an HTTP 201 Created that gives the 
            // location of the created run (in a Location header), 
            // http://${server}:${port}/tavernaserver/rest/runs/UUID (where UUID 
            // is a unique string that identifies the particular run; this is 
            // also the name of the run that you would use in the SOAP 
            // interface). Note that the run is not yet actually doing anything.
            // Here we take the workflow available as resource in 
            // src/main/resources/eu/scape_project/tb/taverna/rest/test.t2flow
            // as example, normally it is passed as a File object.
            InputStream workflowResource = TavernaServerRestClientDemo.class.getResourceAsStream("test.t2flow");
            File workflowFile = File.createTempFile("tavernaworkflow", ".t2flow");
            FileOutputStream fos = new FileOutputStream(workflowFile);
            IOUtils.copyLarge(workflowResource, fos);
            String uuidBaseResourceUrl = tsrc.submitWorkflow(workflowFile);
            logger.info(uuidBaseResourceUrl);

            // 2. PUT INPUT PORT
            // Next, you need to set up the inputs to the workflow ports. To set 
            // the input port, FOO, to have the value BAR, you would PUT a 
            // message like this to the URI 
            // http://${server}:${port}/tavernaserver/rest/runs/UUID/input/input/FOO
            //
            // <t2sr:runInput xmlns:t2sr="http://ns.taverna.org.uk/2010/xml/server/rest/">
            //   <t2sr:value>BAR</t2sr:value>
            // </t2sr:runInput>
            URL resourceUrl = new URL(uuidBaseResourceUrl);
            tsrc.setWorkflowInput(resourceUrl, "in", "Hello World!");

            // 3. PUT STATUS
            // Now you can start the file running. This is done by using a PUT 
            // to set http://${server}:${port}/tavernaserver/rest/runs/UUID/status 
            // to the plain text value "Operating".
            tsrc.setWorkflowStatus(resourceUrl, TavernaWorkflowStatus.OPERATING);

            // 4. GET STATUS
            // Now you need to poll, waiting for the workflow to finish. To 
            // discover the state of a run, you can (at any time) do a GET on 
            // http://${server}:${port}/tavernaserver/rest/runs/UUID/status; 
            // when the workflow has finished executing, this will return 
            // Finished instead of Operating (or Initialized, the starting 
            // state).
            // Polling for a maximum of 30 seconds every 1 second
            int i = 0;
            while (i < 30) {
                try {
                    if (tsrc.getWorkflowStatus(resourceUrl) == TavernaWorkflowStatus.FINISHED) {
                        logger.info("Workflow finished.");
                        break;
                    }
                    Thread.sleep(3000); // polling every 3 seconds
                } catch (InterruptedException e) {
                }
                i++;
            }

            // GET CURRENT RUNS
            tsrc.getCurrentTavernaRuns();

            // DELETE RUN(S)
//            tsrc.deleteWorkflow(resourceUrl);
//            tsrc.deleteAllRuns();

            // Shutdown client
//            tsrc.getConnectionManager().shutdown();
        } catch (TavernaClientException ex) {
            logger.error("Error", ex);
        }
    }
}
