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

import eu.scape_project.tb.rest.DefaultHttpAuthRestClient;
import eu.scape_project.tb.rest.DefaultHttpClientException;
import eu.scape_project.tb.rest.xml.XPathEvaluator;
import eu.scape_project.tb.rest.xml.XmlResponseParser;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple Taverna REST client for Taverna Server 2.4. Download server at
 * http://www.taverna.org.uk/download/server/. This client is using the
 * tb-simplerestclient module.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
@Component
public class TavernaServerRestClient extends DefaultHttpAuthRestClient {

    private static Logger logger = LoggerFactory.getLogger(TavernaServerRestClient.class.getName());
    private SimpleBasicHttpAuthenticator authenticator;
    private int httpsReplacePort;

    /**
     * Constructor of the taverna server rest client. Constructor parameters are
     * autowired, see src/main/resources/tavernaconfig.properties.
     *
     * @param host Host
     * @param port Port
     * @param basePath Base path
     */
    public TavernaServerRestClient(String scheme, String host, int port, String basePath) {
        super(scheme, host, port, basePath);
        logger.info("Creating client, scheme: " + scheme + ", host: " + host + ", port: " + port + ", basepath: " + basePath);
    }

    /**
     * Setter of the user field (autowired, see
     * src/main/resources/tavernaconfig.properties)
     *
     * @param user
     */
    @Override
    public void setUser(String user) {
        logger.info("Setting user name: " + user);
        this.user = user;
        if (password != null) {
            logger.info("Setting credentials");
            this.setCredentials();
            this.initAuthenticator();
        }
    }

    /**
     * Setter of the password field (autowired, see
     * src/main/resources/tavernaconfig.properties)
     *
     * @param user
     */
    @Override
    public void setPassword(String password) {
        logger.info("Setting password: " + password);
        this.password = password;
        if (user != null) {
            logger.info("Setting credentials");
            this.setCredentials();
            this.initAuthenticator();
        }
    }

    /**
     * Init simple basic HTTP authenticator after user and password are set.
     */
    private void initAuthenticator() {
        this.authenticator = new SimpleBasicHttpAuthenticator(this.user, this.password);
    }

    /**
     * Replace HTTPS port when using Taverna in insecure mode. The Taverna
     * Server returns HTTPS URLs in insecure mode. The HTTPS port, e.g. 8443, is
     * then replaced by the HTTP port, e.g. 8080, for follow-up requests based
     * on URLs contained in the REST response of the server. This allows
     * converting a "secure" HTTPS REST resource URL:
     * https://${server}:8443/TavernaServer.2.4.1/rest/runs to an "insecure"
     * HTTP REST resource URL:
     * http://${server}:8080/TavernaServer.2.4.1/rest/runs
     *
     * @return HTTPS port
     */
    public int getHttpsReplacePort() {
        return httpsReplacePort;
    }

    /**
     * Replace HTTPS port when using Taverna in insecure mode. The Taverna
     * Server returns HTTPS URLs in insecure mode. The HTTPS port, e.g. 8443, is
     * then replaced by the HTTP port, e.g. 8080, for follow-up requests based
     * on URLs contained in the REST response of the server. This allows
     * converting a "secure" HTTPS REST resource URL:
     * https://${server}:8443/TavernaServer.2.4.1/rest/runs to an "insecure"
     * HTTP REST resource URL:
     * http://${server}:8080/TavernaServer.2.4.1/rest/runs
     *
     * @param httpsReplacePort HTTPS port
     */
    public void setHttpsReplacePort(int httpsReplacePort) {
        this.httpsReplacePort = httpsReplacePort;
    }

    /**
     * Set the input port value of an initialised workflow run.
     *
     * @param uuidBaseUrl UUID base URL of the workflow run, i.e.
     * http://${server}:${port}/tavernaserver/rest/runs/UUID
     * @param inputPort Name of the input port
     * @param value Value to be set for the input port
     */
    public void setWorkflowInput(URL uuidBaseUrl, String inputPort, String value) throws TavernaClientException {
        try {
            String putContent = "<t2sr:runInput "
                    + "xmlns:t2sr=\"http://ns.taverna.org.uk/2010/xml/server/rest/\">\n"
                    + "\t<t2sr:value>" + value + "</t2sr:value>\n"
                    + "</t2sr:runInput>\n";

            String urlStr = getAdaptedSchemeUrl(uuidBaseUrl.toString());
            URL resourceUrl = new URL(urlStr + "/input/input/" + inputPort);
            HttpResponse response = this.executePut(resourceUrl, putContent, ContentType.APPLICATION_XML);
            int code = response.getStatusLine().getStatusCode();
            if (code != 200) {
                throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
            }
            this.consume(response);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        } catch (DefaultHttpClientException e) {
            throw new TavernaClientException("HTTP Client exception");
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
    public void setWorkflowInput(String uuid, String inputPort, String value) throws TavernaClientException {
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
    public void setWorkflowStatus(URL uuidBaseUrl, TavernaWorkflowStatus status) throws TavernaClientException {
        String uuidBaseUrlStr = this.getAdaptedSchemeUrl(uuidBaseUrl.toExternalForm());
        try {
            URL resourceUrl = new URL(uuidBaseUrlStr + "/status");
            HttpResponse response = this.executePut(resourceUrl, status.toString(), ContentType.TEXT_PLAIN);
            int code = response.getStatusLine().getStatusCode();
            if (code != 200) {
                throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
            }
            this.consume(response);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        } catch (DefaultHttpClientException e) {
            throw new TavernaClientException("HTTP Client exception");
        }
    }

    /**
     * Set the workflow status of a workflow run.
     *
     * @param uuid UUID of the workflow run (UUID base URL of the workflow run
     * is created)
     * @param status Status of the workflow run to be set
     */
    public void setWorkflowStatus(String uuid, TavernaWorkflowStatus status) throws TavernaClientException {
        try {
            URL uuidBaseUrl = new URL(this.getBaseUrlStr() + "/rest/runs/" + uuid);
            this.setWorkflowStatus(uuidBaseUrl, status);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        }
    }

    /**
     * Get workflow status.
     *
     * @param uuidBaseUrl UUID base url
     * @return Workflow status
     * @throws HttpException
     */
    public TavernaWorkflowStatus getWorkflowStatus(URL uuidBaseUrl) throws TavernaClientException {
        TavernaWorkflowStatus status = null;
        String uuid = TavernaRestUtil.getUUIDfromUUIDResourceURL(uuidBaseUrl.toExternalForm());
        ArrayList<String> runs = getCurrentTavernaRuns();
        if (!runs.contains(uuid)) {
            status = TavernaWorkflowStatus.NONEXISTENT;
            return status;
        }
        String uuidBaseUrlStr = this.getAdaptedSchemeUrl(uuidBaseUrl.toExternalForm());
        URL statusUrl;
        try {
            statusUrl = new URL(uuidBaseUrlStr + "/status");
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
            status = TavernaWorkflowStatus.ERROR;
            return status;
        }
        HttpResponse response = this.executeGet(statusUrl, "text/plain");
        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
        }
        String statusStr;
        BasicManagedEntity sr = (BasicManagedEntity) response.getEntity();
        try {
            InputStream content = sr.getContent();
            statusStr = IOUtils.toString(content);
        } catch (IOException ex) {
            logger.error("Error while reading response.", ex);
            status = TavernaWorkflowStatus.ERROR;
            return status;
        }
        logger.info("Status: " + statusStr);
        statusStr = statusStr.replaceAll("\\s", "");
        if (statusStr.equals("Finished")) {
            return TavernaWorkflowStatus.FINISHED;
        } else if (statusStr.equals("Operating")) {
            return TavernaWorkflowStatus.OPERATING;
        } else if (statusStr.equals("Initialized")) {
            return TavernaWorkflowStatus.INITIALISED;
        }
        this.consume(response);
        return status;
    }

    /*
     */
    public TavernaWorkflowStatus getWorkflowStatus(String uuid) throws TavernaClientException {
        TavernaWorkflowStatus status;
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
    public ArrayList<String> getCurrentTavernaRuns() throws TavernaClientException {
        ArrayList<String> uuids = new ArrayList<String>();
        try {
            HttpResponse response = this.executeGet("runs", "application/xml");
            int code = response.getStatusLine().getStatusCode();
            if (code != 200) {
                throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
            }
            XmlResponseParser rp = new XmlResponseParser(response);
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
        } catch (DefaultHttpClientException e) {
            throw new TavernaClientException("HTTP Client exception");
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
    public String submitWorkflow(File workflowFile) throws TavernaClientException {

        HttpResponse response = null;
        try {
            response = this.executeFileContentPost("runs", workflowFile, ContentType.create("application/vnd.taverna.t2flow+xml"));
        } catch (DefaultHttpClientException e) {
            throw new TavernaClientException("HTTP Client exception: " + e.getMessage());
        }
        int code = response.getStatusLine().getStatusCode();
        if (code != 201) {
            throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
        }
        String restLocation = null;
        if (response.containsHeader("Location")) {
            Header[] locationHeaders = response.getHeaders("Location");
            Header locationHeader = locationHeaders[0];
            restLocation = locationHeader.getValue();
        }
        this.consume(response);
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
    public void deleteWorkflow(URL uuidBaseUrl) throws TavernaClientException {
        String uuidBaseUrlStr = this.getAdaptedSchemeUrl(uuidBaseUrl.toExternalForm());
        HttpResponse response = null;
        try {
            response = this.executeDelete(uuidBaseUrlStr);
        } catch (DefaultHttpClientException e) {
            throw new TavernaClientException("HTTP Client exception");
        }
        int code = response.getStatusLine().getStatusCode();
        if (code != 204) {
            throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
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
    public void deleteWorkflow(String uuid) throws TavernaClientException {
        try {
            URL uuidBaseUrl = new URL(this.getBaseUrlStr() + "/runs/" + uuid);
            this.deleteWorkflow(uuidBaseUrl);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
        }
    }

    void deleteAllRuns() throws TavernaClientException {
        ArrayList<String> runs = getCurrentTavernaRuns();
        for (String uuid : runs) {
            deleteWorkflow(uuid);
        }
    }

    /**
     * Get workflow status.
     *
     * @param uuidBaseUrl UUID base url
     * @return Workflow status
     * @throws HttpException TODO: Remove!
     */
    public List<KeyValuePair> getWorkflowRunOutputValues(URL uuidBaseUrl) throws TavernaClientException {
        URL outputRestUrl;
        String uuidBaseUrlStr = this.getAdaptedSchemeUrl(uuidBaseUrl.toExternalForm());
        /*
         * TODO: Remove!
         */
        uuidBaseUrlStr = uuidBaseUrlStr.replace("fue-hdc01:8080", "fue-l:80");
        try {
            outputRestUrl = new URL(uuidBaseUrlStr + "/output");
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
            return null;
        }
        HttpResponse response = this.executeGet(outputRestUrl, "application/xml");
        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
        }
        TavernaResponseParser trp = new TavernaResponseParser(response);

        trp.setAuthenticator(this.authenticator);
        return trp.getResponseOutputValues();
    }

    /**
     * Converts a https url to a http url in case the Taverna Server is
     * configured using the insecure profile, but returns a https URL. Server
     * returns a https URL on a http request.
     *
     * @param httpsSchemeUrl https scheme url
     * @return http scheme url
     */
    private String getAdaptedSchemeUrl(String httpsSchemeUrl) {
        if (this.getScheme().equals("https")) {
            return httpsSchemeUrl;
        }
        String httpSchemeUrl = httpsSchemeUrl.replace("https://", "http://");
        httpSchemeUrl = httpSchemeUrl.replace(":" + httpsReplacePort + "/", ":" + this.getPort() + "/");
        logger.info("Changing https URL " + httpsSchemeUrl + " to http URL " + httpSchemeUrl);
        return httpSchemeUrl;
    }

    public String getWorkflowRunTavernaLog(URL uuidBaseUrl) throws TavernaClientException, IOException {

        URL outputRestUrl;
        String uuidBaseUrlStr = this.getAdaptedSchemeUrl(uuidBaseUrl.toExternalForm());
        /*
         * TODO: Remove!
         */
        //uuidBaseUrlStr = uuidBaseUrlStr.replace("fue-hdc01:8080", "fue-l:80");
        try {
            outputRestUrl = new URL(uuidBaseUrlStr + "/wd/logs/detail.log");
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error", ex);
            return null;
        }
        HttpResponse response = this.executeGet(outputRestUrl, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            throw new TavernaClientException("HTTP status code: " + response.getStatusLine().toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        
        try {
            InputStream is = response.getEntity().getContent();

            InputStreamReader inR = new InputStreamReader(is);
            BufferedReader buf = new BufferedReader(inR);
            String line;
            while ((line = buf.readLine()) != null) {
                stringBuilder.append(line+"\n");
            }
        } catch (Exception ex) {
            throw new TavernaClientException(ex.getMessage());
        } finally {
            EntityUtils.consume(response.getEntity());
        }
        return stringBuilder.toString();



        //http://fue-hdc01:8080/TavernaServer.2.4.1/rest/runs/3a4622ca-d98d-4f05-a142-89a39db31387
    }
}
