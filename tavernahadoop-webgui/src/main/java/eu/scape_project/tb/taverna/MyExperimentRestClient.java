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

import eu.scape_project.tb.config.MyExperimentConfig;
import eu.scape_project.tb.config.TavernaConfig;
import eu.scape_project.tb.model.entity.Workflow;
import eu.scape_project.tb.model.entity.WorkflowRun;
import eu.scape_project.tb.model.factory.WorkflowFactory;
import eu.scape_project.tb.rest.DefaultHttpAuthRestClient;
import eu.scape_project.tb.rest.DefaultHttpClientException;
import eu.scape_project.tb.rest.util.FileUtility;
import eu.scape_project.tb.rest.xml.XPathEvaluator;
import eu.scape_project.tb.rest.xml.XmlResponseParser;
import eu.scape_project.tb.taverna.rest.SimpleBasicHttpAuthenticator;
import eu.scape_project.tb.taverna.rest.TavernaClientException;
import eu.scape_project.tb.taverna.rest.TavernaServerRestClient;
import eu.scape_project.tb.taverna.rest.TavernaWorkflowStatus;
import eu.scape_project.tb.util.StringUtil;
import java.io.*;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Web application taverna rest client. Singleton class for the
 * tavernahttp-restclient module which performs requests to the Taverna Server.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class MyExperimentRestClient implements Serializable {

    private Logger logger = LoggerFactory.getLogger(MyExperimentRestClient.class.getName());
    private MyExperimentConfig config;
    private final DefaultHttpAuthRestClient myExpRestClient;
    // Singleton Instance
    private static MyExperimentRestClient instance = null;
    private String host;
    private String user;
    private String password;
    private String myExperimentSession;

    public static MyExperimentRestClient getInstance() throws DefaultHttpClientException {
        if (instance == null) {
            instance = new MyExperimentRestClient();
            instance.createNewMyExperimentSession();
        }
        return instance;
    }

    private MyExperimentRestClient() throws DefaultHttpClientException {
        config = new MyExperimentConfig();
        host = config.getProp("myexperiment.host");
        user = config.getProp("myexperiment.username");
        password = config.getProp("myexperiment.password");
        myExpRestClient = new DefaultHttpAuthRestClient("http", host, 80, "");
        myExpRestClient.setUser(user);
        myExpRestClient.setPassword(password);
    }

    public DefaultHttpAuthRestClient getClient() {
        return myExpRestClient;
    }

    public String getMyExperimentSession() {
        return myExperimentSession;
    }

    public void setNewMyExperimentSession() {
    }

    public void createNewMyExperimentSession() throws DefaultHttpClientException {
        // get myExperiment session 
        String myExperimentSessionStr = null;
        try {
            String sessRequest = "<session><username>" + user + "</username><password>" + password + "</password></session>";
            HttpResponse sessionResponse = myExpRestClient.executeStringPost("/session", sessRequest, ContentType.APPLICATION_XML);
            InputStream is = sessionResponse.getEntity().getContent();
            Header[] headers = sessionResponse.getAllHeaders();

            for (int i = 0; i < headers.length; i++) {
                Header h = headers[i];
                if (h.getName().equals("Set-Cookie")) {
                    String val = h.getValue();
                    int end = val.indexOf(";");
                    myExperimentSessionStr = val.substring(0, end);
                }
            }
            EntityUtils.consume(sessionResponse.getEntity());
            myExperimentSession = myExperimentSessionStr;
        } catch (Exception ex) {
            throw new DefaultHttpClientException(ex.getMessage());
        }
    }

    public Workflow getMyExperimentWorkflow(int wfId) throws DefaultHttpClientException {
        Workflow wf = new Workflow();
        wf.setWfid(wfId);
        String fileName = wfId+".t2flow";
        wf.setFilename(fileName);
        
        
        // get workflow metadata
        if (myExperimentSession != null) {
            String contentUri = null;
            HttpResponse response = myExpRestClient.executeGet("/workflow.xml?id=" + wfId, "application/xml", myExperimentSession);
            XmlResponseParser responseParser = new XmlResponseParser(response);
            if (responseParser == null) {
                throw new DefaultHttpClientException("Error while initialising response parser");
            }
            responseParser.parseResponse();
            if (responseParser.isIsParsed()) {
                XPathEvaluator xPathEval = new XPathEvaluator(responseParser);
                NodeList nl = xPathEval.evaluate("/workflow/description");
                if (nl.getLength() == 1) {
                    Node n = nl.item(0);
                    String wfDescription = StringUtil.txtToHtml(n.getTextContent());
                    logger.info("Workflow description:\n" + wfDescription);
                    wf.setDescription(host);
                }
                NodeList uriNl = xPathEval.evaluate("/workflow/content-uri");
                if (uriNl.getLength() == 1) {
                    Node n = uriNl.item(0);
                    contentUri = n.getTextContent();
                    logger.info("Workflow content URI: " + contentUri);
                }
            }
            if (contentUri != null) {
                wf.setMyExperimentContentUri(contentUri);
            }
        } else {
            throw new DefaultHttpClientException("MyExperiment-Session is not initialised!");
        }
        return wf;
    }

    public InputStream getMyExperimentWorkflowContentStream(Workflow wf) throws DefaultHttpClientException {
        try {
            if (myExperimentSession != null) {
                if (wf != null) {
                    String contentUri = wf.getMyExperimentContentUri();
                    if (contentUri != null) {
                        URL url = new URL(wf.getMyExperimentContentUri());
                        HttpResponse wfResponse = myExpRestClient.executeGet(url, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", myExperimentSession);
                        return wfResponse.getEntity().getContent();
                    } else {
                        logger.error("Invalid workflow or content uri is empty");
                        return null;
                    }
                } else {
                    logger.error("Invalid workflow object!");
                    return null;
                }
            } else {
                logger.error("MyExperiment-Session is not initialised!");
                return null;
            }
        } catch (Exception ex) {
            throw new DefaultHttpClientException(ex.getMessage());
        }
    }
}
