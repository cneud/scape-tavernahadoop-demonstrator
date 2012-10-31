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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the simple http rest client. This client provides a simple http
 * rest client with a small set of REST request methods defined in the
 * DefaultHttpRestClient class. It also includes a basic xml parser for
 * processing an XML response.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class DefaultHttpRestClientDemo {

    private static Logger logger = LoggerFactory.getLogger(DefaultHttpRestClientDemo.class.getName());

    /**
     * Main method of the simple http rest client.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // Example: Google search REST API
        // http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=Google%20REST%20API
        DefaultHttpRestClient simpleRestClient = new DefaultHttpRestClient("ajax.googleapis.com", 80, "/ajax/services/search");
        simpleRestClient.executeGet("web?v=1.0&q=Google%20REST%20API");
    }
}
