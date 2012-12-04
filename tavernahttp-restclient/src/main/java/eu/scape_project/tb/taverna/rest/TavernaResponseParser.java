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

import eu.scape_project.tb.rest.xml.XPathEvaluator;
import eu.scape_project.tb.rest.xml.XmlResponseParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Taverna response parser. XML Response parser for processing response messages
 * of the taverna server.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TavernaResponseParser {

    private static Logger logger = LoggerFactory.getLogger(TavernaResponseParser.class.getName());
    private XPathEvaluator xPathEval;
    private SimpleBasicHttpAuthenticator authenticator;

    TavernaResponseParser(HttpResponse response) {
        XmlResponseParser rp = new XmlResponseParser(response);
        rp.parseResponse();
        xPathEval = new XPathEvaluator(rp);
    }

    void setAuthenticator(SimpleBasicHttpAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * Get the list of output values from the Taverna server response XML
     * message. Parses the XML response of the GET request to
     * http://${server}:${port}/TavernaServer.2.4.1/rest/runs/${UUID}/output,
     * extracts the output value nodes and reads the content.
     *
     * @return List of "output port name"-"output port value" key-value pair.
     */
    List<KeyValuePair> getResponseOutputValues() {
        if (authenticator == null) {
            logger.error("Basic HTTP Authentication required.");
            return null;
        }
        List<KeyValuePair> resultList = new ArrayList<KeyValuePair>();
        KeyValuePair keyValuePair;
        NodeList nl = xPathEval.evaluate("/workflowOutputs/output");
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                InputStream in;
                Node n = nl.item(i);
                NamedNodeMap outputAttributes = n.getAttributes();
                String portName = outputAttributes.getNamedItem("ns1:name").getNodeValue();
                Node valueNode = n.getFirstChild();
                NamedNodeMap valueAttributes = valueNode.getAttributes();
                String hrefStr = valueAttributes.getNamedItem("ns2:href").getNodeValue();
                Authenticator.setDefault(authenticator);
                in = new URL(hrefStr).openStream();
                String result = "";
                InputStreamReader inR = new InputStreamReader(in);
                BufferedReader buf = new BufferedReader(inR);
                String line;
                while ((line = buf.readLine()) != null) {
                    result += line + "\n";
                }
                keyValuePair = new KeyValuePair(portName, result);
                resultList.add(keyValuePair);
            } catch (IOException ex) {
                logger.error("I/O error", ex);
            }
        }
        return resultList;
    }
}
