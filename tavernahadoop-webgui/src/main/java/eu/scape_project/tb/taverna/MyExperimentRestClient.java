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
import eu.scape_project.tb.model.entity.Workflow;
import eu.scape_project.tb.rest.DefaultHttpAuthRestClient;
import eu.scape_project.tb.rest.DefaultHttpClientException;
import eu.scape_project.tb.rest.xml.XPathEvaluator;
import eu.scape_project.tb.rest.xml.XmlResponseParser;
import eu.scape_project.tb.util.StringUtil;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
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

    /**
     * Singleton creator
     *
     * @return Singleton instance of this class
     * @throws DefaultHttpClientException
     */
    public static MyExperimentRestClient getInstance() throws DefaultHttpClientException {
        if (instance == null) {
            instance = new MyExperimentRestClient();
            instance.createNewMyExperimentSession();
        }
        return instance;
    }

    /**
     * Private constructor is called by singleton creator.
     *
     * @throws DefaultHttpClientException
     */
    private MyExperimentRestClient() throws DefaultHttpClientException {
        config = new MyExperimentConfig();
        host = config.getProp("myexperiment.host");
        user = config.getProp("myexperiment.username");
        password = config.getProp("myexperiment.password");
        myExpRestClient = new DefaultHttpAuthRestClient("http", host, 80, "");
        myExpRestClient.setUser(user);
        myExpRestClient.setPassword(password);
    }

    /**
     * Getter for the http client.
     * @return http client
     */
    public DefaultHttpAuthRestClient getClient() {
        return myExpRestClient;
    }

    /**
     * Getter for the myExperiment session
     * @return myExperiment session
     */
    public String getMyExperimentSession() {
        return myExperimentSession;
    }

    /**
     * Setter for the myExperiment session
     */
    public void setNewMyExperimentSession(String myExperimentSession) {
        this.myExperimentSession = myExperimentSession;
    }

    /**
     * Intialise myExperiment session
     *
     * @return Session handler string
     */
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

    /**
     * Get workflow xml description from myExperiment
     *
     * @return Workflow object
     */
    public Workflow getMyExperimentWorkflow(int wfId) throws DefaultHttpClientException {
        Workflow wf = new Workflow();
        wf.setWfid(wfId);
        String fileName = wfId + ".t2flow";
        wf.setFilename(fileName);
        // get workflow metadata
        if (myExperimentSession != null) {
            HttpResponse response = myExpRestClient.executeGet("/workflow.xml?id=" + wfId, "application/xml", myExperimentSession);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 404) {
                throw new DefaultHttpClientException("Workflow "+wfId+" is not available on myExperiment.");
            }
            XmlResponseParser responseParser = new XmlResponseParser(response);
            if (responseParser == null) {
                throw new DefaultHttpClientException("Error while initialising response parser");
            }
            responseParser.parseResponse();
            if (responseParser.isIsParsed()) {
                XPathEvaluator xPathEval = new XPathEvaluator(responseParser);
                wf.setTitle(getXmlDescrWorkflowProperty(xPathEval, "/workflow/title", false));
                wf.setDescription(getXmlDescrWorkflowProperty(xPathEval, "/workflow/description", true));
                wf.setMyExperimentContentUri(getXmlDescrWorkflowProperty(xPathEval, "/workflow/content-uri", false));
                wf.setMyExperimentPreviewUri(getXmlDescrWorkflowProperty(xPathEval, "/workflow/preview", false));
            }
        } else {
            throw new DefaultHttpClientException("MyExperiment-Session is not initialised!");
        }
        return wf;
    }

    /**
     * Get xml node text
     *
     * @param xPathEval XPath expression evaluator
     * @param xpath XPath expression
     * @param htmlConv HTML conversion to be applied or not
     * @return xml node text
     */
    private String getXmlDescrWorkflowProperty(XPathEvaluator xPathEval, String xpath, boolean htmlConv) {
        NodeList previewNl = xPathEval.evaluate(xpath);
        String nodeText = null;
        if (previewNl.getLength() == 1) {
            Node n = previewNl.item(0);
            nodeText = n.getTextContent();
            logger.info("Workflow content URI: " + nodeText);
            if (htmlConv) {
                nodeText = StringUtil.txtToHtml(nodeText);
            }
        }
        return nodeText;
    }

    /**
     * Get workflow t2flow input stream from content-uri
     *
     * @param wf Workflow
     * @return t2flow input stream
     */
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
                        throw new DefaultHttpClientException("Invalid workflow or content uri is empty");
                    }
                } else {
                    throw new DefaultHttpClientException("Invalid workflow object!");
                }
            } else {
                throw new DefaultHttpClientException("MyExperiment-Session is not initialised!");
            }
        } catch (Exception ex) {
            throw new DefaultHttpClientException(ex.getMessage());
        }
    }
}
