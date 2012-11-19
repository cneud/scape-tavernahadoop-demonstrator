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

import eu.scape_project.tb.taverna.RunDescription;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unmarshaller of the Taverna XML REST response messages.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class XmlResponseUnmarshaller {

    private static Logger logger = LoggerFactory.getLogger(XmlResponseUnmarshaller.class.getName());

    /**
     * Unmarshaller of the run descriptionXML response.
     *
     * @param xmlResponseIs Run descriptionXML response input stream
     * @return Run description object
     * @throws TavernaClientException
     */
    public static RunDescription unmarshall(InputStream xmlResponseIs) throws TavernaClientException {
        RunDescription runDesc = null;
        try {
            JAXBContext context;
            context = JAXBContext.newInstance("eu.scape_project.tb.taverna");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            runDesc = (RunDescription) unmarshaller.unmarshal(xmlResponseIs);
        } catch (JAXBException ex) {
            logger.error("JAXBException", ex);
            throw new TavernaClientException("JAXB error");
        }
        return runDesc;
    }
}
