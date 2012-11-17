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
package eu.scape_project.tb.rest.xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

/**
 * XPath evaluation of a parsed response object (DOM document). A parsed
 * response object (DOM document) is required before the XPath expression can be
 * evaluated.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class XPathEvaluator {

    private static Logger logger = LoggerFactory.getLogger(XPathEvaluator.class.getName());
    private XPath xpath;
    private XmlResponseParser rp;

    /**
     * Constructor of the XPath evaluator class.
     *
     * @param rp Response parser
     */
    public XPathEvaluator(XmlResponseParser rp) {
        this.rp = rp;
        xpath = XPathFactory.newInstance().newXPath();
    }

    /**
     * Evaluate XPath expression that returns a node list.
     *
     * @param xPathExpr
     * @return
     */
    public NodeList evaluate(String xPathExpr) {
        if (!rp.isIsParsed()) {
            logger.error("Unable to evaluate xpath expression, parse result first!");
            return null;
        }
        NodeList items = null;
        try {
            items = (NodeList) xpath.evaluate(xPathExpr, rp.getResponseXml(),
                    XPathConstants.NODESET);
            logger.info("Number of items: " + items.getLength());
        } catch (XPathExpressionException ex) {
            logger.error("XPath Expression Error", ex);
        }
        return items;
    }
}
