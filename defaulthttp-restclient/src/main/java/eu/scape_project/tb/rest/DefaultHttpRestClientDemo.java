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

import eu.scape_project.tb.rest.ssl.DefaultConnectionManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        
        // The examples require a running instance of the Taverna Server 2.4.1
        // http://dev.mygrid.org.uk/wiki/display/taverna/A+Beginner%27s+Installation+Guide+to+Taverna+Server

        // INSECURE
        // Example: Taverna Server REST API (list runs)
        // http://localhost:8080/TavernaServer.2.4.1/rest/runs
        DefaultHttpAuthRestClient insecureRestClient = new DefaultHttpAuthRestClient("http", "localhost", 8080, "/TavernaServer.2.4.1/rest");
        insecureRestClient.setUser("taverna");
        insecureRestClient.setPassword("taverna");
//        insecureRestClient.setAuthContext();
        insecureRestClient.executeGet("/runs", "application/xml");
        
        // SECURE
        // Example: Taverna Server REST API (list runs)
        // https://localhost:8443/TavernaServer.2.4.1/rest/runs
        DefaultHttpAuthRestClient secureRestClient = new DefaultHttpAuthRestClient(DefaultConnectionManager.getInstance(), "https", "localhost", 8443, "/TavernaServer.2.4.1/rest");
        secureRestClient.setUser("taverna");
        secureRestClient.setPassword("taverna");
//        secureRestClient.setAuthContext();
        secureRestClient.executeGet("/runs", "application/xml");
    }
}
