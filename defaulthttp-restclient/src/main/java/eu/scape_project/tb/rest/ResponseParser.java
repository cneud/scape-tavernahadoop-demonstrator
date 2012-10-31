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
package eu.scape_project.tb.rest;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Generic parser for an XML response. Parser creates a DOM Document out of the
 * response object.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class ResponseParser {

    private static Logger logger = LoggerFactory.getLogger(ResponseParser.class.getName());
    private HttpResponse response;
    private Document responseXml;
    private boolean isParsed;

    /**
     * Constructor of the response parser.
     *
     * @param response
     */
    public ResponseParser(HttpResponse response) {
        this.response = response;
        isParsed = false;
    }

    /**
     * Parse response and create DOM document.
     */
    public void parseResponse() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            responseXml = db.parse(response.getEntity().getContent());
        } catch (SAXException ex) {
            logger.error("SAX error", ex);
        } catch (IOException ex) {
            logger.error("IO error", ex);
        } catch (ParserConfigurationException ex) {
            logger.error("Parser Configuration error", ex);
        }
        isParsed = true;
    }

    /**
     * Getter for the response DOM document
     *
     * @return DOM document
     */
    public Document getResponseXml() {
        return responseXml;
    }

    /**
     * Getter for the parsing status.
     *
     * @return parsing status
     */
    public boolean isIsParsed() {
        return isParsed;
    }
}
