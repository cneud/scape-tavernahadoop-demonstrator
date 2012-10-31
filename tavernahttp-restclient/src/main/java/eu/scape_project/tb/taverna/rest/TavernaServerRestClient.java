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

import eu.scape_project.tb.rest.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple Taverna REST client for Taverna Server 2.4. Download server at
 * http://www.taverna.org.uk/download/server/. This client is using the
 * tb-simplerestclient as a module.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
@Component
public class TavernaServerRestClient extends DefaultHttpAuthRestClient {

    private static Logger logger = LoggerFactory.getLogger(TavernaServerRestClient.class.getName());

    /**
     * Constructor of the taverna server rest client. Constructor parameters are
     * autowired, see src/main/resources/tavernaconfig.properties.
     *
     * @param host Host
     * @param port Port
     * @param basePath Base path
     * @param restPath
     */
    public TavernaServerRestClient(String host, int port, String basePath) {
        super(host, port, basePath);
        logger.info("host: " + host + ", port: " + port + ", basepath: " + basePath);
    }

    /**
     * Setter of the user field (autowired, see
     * src/main/resources/tavernaconfig.properties)
     *
     * @param user
     */
    @Autowired
    @Override
    public void setUser(String user) {
        logger.info("Autowiring User name: " + user);
        this.user = user;
        if (password != null) {
            logger.info("Setting credentials");
            this.setCredentials();
        }
    }

    /**
     * Setter of the password field (autowired, see
     * src/main/resources/tavernaconfig.properties)
     *
     * @param user
     */
    @Autowired
    @Override
    public void setPassword(String password) {
        logger.info("Autowiring password: " + password);
        this.password = password;
        if (user != null) {
            logger.info("Setting credentials");
            this.setCredentials();
        }
    }

    /**
     * Set the input port value of an initialised workflow run.
     *
     * @param uuidBaseUrl UUID base URL of the workflow run, i.e.
     * http://${server}:${port}/tavernaserver/rest/runs/UUID
     * @param inputPort Name of the input port
     * @param value Value to be set for the input port
     */
    public void setWorkflowInput(URL uuidBaseUrl, String inputPort, String value) throws HttpException {
        try {
            String putContent = "<t2sr:runInput "
                    + "xmlns:t2sr=\"http://ns.taverna.org.uk/2010/xml/server/rest/\">\n"
                    + "\t<t2sr:value>" + value + "</t2sr:value>\n"
                    + "</t2sr:runInput>\n";
            URL resourceUrl = new URL(uuidBaseUrl.toString() + "/input/input/" + inputPort);
            HttpResponse response = this.executePut(resourceUrl, putContent, ContentType.APPLICATION_XML);
            int code = response.getStatusLine().getStatusCode();
            if (code != 200) {
                this.throwHttpError(response.getStatusLine().toString());
            }
            this.consumeResponseEntityContent(response);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        }
    }

    /**
     * Set the input port value of an initialised workflow run.
     *
     * @param uuid UUID of the workflow run (UUID base URL of the workflow run
     * is created)
     * @param inputPort Name of the input port
     * @param value Value to be set for the input port
     */
    public void setWorkflowInput(String uuid, String inputPort, String value) throws HttpException {
        try {
            URL uuidBaseUrl = new URL(this.getBaseUrlStr() + "/rest/runs/" + uuid);
            this.setWorkflowInput(uuidBaseUrl, inputPort, value);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        }
    }
    
    

    /**
     * Set the workflow status of a workflow run.
     *
     * @param uuidBaseUrl UUID base URL of the workflow run, i.e.
     * http://${server}:${port}/tavernaserver/rest/runs/UUID
     * @param status Status of the workflow run to be set
     */
    public void setWorkflowStatus(URL uuidBaseUrl, TavernaWorkflowStatus status) throws HttpException {
        try {
            URL resourceUrl = new URL(uuidBaseUrl.toString() + "/status");
            HttpResponse response = this.executePut(resourceUrl, status.toString(), ContentType.TEXT_PLAIN);
            int code = response.getStatusLine().getStatusCode();
            if (code != 200) {
                this.throwHttpError(response.getStatusLine().toString());
            }
            this.consumeResponseEntityContent(response);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        }
    }

    /**
     * Set the workflow status of a workflow run.
     *
     * @param uuid UUID of the workflow run (UUID base URL of the workflow run
     * is created)
     * @param status Status of the workflow run to be set
     */
    public void setWorkflowStatus(String uuid, TavernaWorkflowStatus status) throws HttpException {
        try {
            URL uuidBaseUrl = new URL(this.getBaseUrlStr() + "/rest/runs/" + uuid);
            this.setWorkflowStatus(uuidBaseUrl, status);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        }
    }
    
    /*
     */
    public TavernaWorkflowStatus getWorkflowStatus(URL uuidBaseUrl) throws HttpException {
        TavernaWorkflowStatus status = null;
        URL statusUrl = null;
        try {
            statusUrl = new URL(uuidBaseUrl+"/status");
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
            return null;
        }
        HttpResponse response = this.executeGet(statusUrl);
        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            this.throwHttpError(response.getStatusLine().toString());
        }
        String statusStr = "";
        BasicManagedEntity sr = (BasicManagedEntity) response.getEntity();
        try {
            InputStream content = sr.getContent();
            statusStr = IOUtils.toString(content);
        } catch (IOException ex) {
            logger.error("Error while reading response.",ex);
        }
        logger.info("Status: "+statusStr);
        if(statusStr.equals("Finished"))
            return TavernaWorkflowStatus.FINISHED;
        else if(statusStr.equals("Operating"))
            return TavernaWorkflowStatus.OPERATING;
        else if(statusStr.equals("Initialised"))
            return TavernaWorkflowStatus.INITIALISED;
        return status;        
    }

    /*
     */
    public TavernaWorkflowStatus getWorkflowStatus(String uuid) throws HttpException {
        TavernaWorkflowStatus status = null;
        try {
            URL uuidBaseUrl = new URL(this.getBaseUrlStr() + "/rest/runs/" + uuid);
            status = this.getWorkflowStatus(uuidBaseUrl);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
            return null;
        }
        return status;        
    }

    /**
     * Get current Taverna run UUIDs. List of all workflows that have been
     * submitted, i.e. if {UUID} is a one-item-list, then the corresponding UUID
     * base URL of the workflow run is
     * http://${server}:${port}/tavernaserver/rest/runs/UUID
     *
     * @return List of Taverna UUID strings
     */
    public ArrayList<String> getCurrentTavernaRuns() throws HttpException {
        ArrayList<String> uuids = new ArrayList<String>();
        HttpResponse response = this.executeGet("runs");
        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            this.throwHttpError(response.getStatusLine().toString());
        }
        ResponseParser rp = new ResponseParser(response);
        rp.parseResponse();
        XPathEvaluator xPathEval = new XPathEvaluator(rp);
        NodeList items = xPathEval.evaluate("/runList/run");
        logger.info(Integer.toString(items.getLength()));
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            String uuid = item.getTextContent();
            logger.info("UUID: " + uuid);
            uuids.add(uuid);
        }
        return uuids;
    }

    /**
     * Submit workflow. Post a Taverna workflow document and return the UUID
     * base URL of the workflow run.
     *
     * @param workflowFile Workflow t2flow file
     * @return UUID base URL of the workflow run, i.e.
     * http://${server}:${port}/tavernaserver/rest/runs/UUID
     */
    public String submitWorkflow(File workflowFile) throws HttpException {
        HttpResponse response = this.executeFileContentPost("runs", workflowFile, ContentType.APPLICATION_TAVERNA_XML);
        int code = response.getStatusLine().getStatusCode();
        if (code != 201) {
            this.throwHttpError(response.getStatusLine().toString());
        }
        String restLocation = null;
        if (response.containsHeader("Location")) {
            Header[] locationHeaders = response.getHeaders("Location");
            Header locationHeader = locationHeaders[0];
            restLocation = locationHeader.getValue();
        }
        this.consumeResponseEntityContent(response);
        return restLocation;
    }

    /**
     * Set the input port value of an initialised workflow run.
     *
     * @param uuidBaseUrl UUID base URL of the workflow run, i.e.
     * http://${server}:${port}/tavernaserver/rest/runs/UUID
     * @param inputPort Name of the input port
     * @param value Value to be set for the input port
     */
    public void deleteWorkflow(URL uuidBaseUrl) throws HttpException {
        HttpResponse response = this.executeDelete(uuidBaseUrl);
        int code = response.getStatusLine().getStatusCode();
        if (code != 204) {
            this.throwHttpError(response.getStatusLine().toString());
        }
    }

    /**
     * Set the input port value of an initialised workflow run.
     *
     * @param uuid UUID of the workflow run (UUID base URL of the workflow run
     * is created)
     * @param inputPort Name of the input port
     * @param value Value to be set for the input port
     */
    public void deleteWorkflow(String uuid) throws HttpException {
        try {
            URL uuidBaseUrl = new URL(this.getBaseUrlStr() + "/runs/" + uuid);
            this.deleteWorkflow(uuidBaseUrl);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        }
    }

    void deleteAllRuns() throws HttpException {
        ArrayList<String> runs = getCurrentTavernaRuns();
        for(String uuid : runs) {
            deleteWorkflow(uuid);
        }
    }
}
