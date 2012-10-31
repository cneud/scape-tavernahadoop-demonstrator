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

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple HTTP Rest client with BASIC authentication. If the request requires
 * BASIC authentication, using this client a username and password can be
 * defined (DefaultHttpAuthRestClient.setCredentials).
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class DefaultHttpAuthRestClient extends DefaultHttpRestClient {

    protected String user;
    protected String password;
    private static Logger logger = LoggerFactory.getLogger(DefaultHttpAuthRestClient.class.getName());

    /**
     * Constructor of the simple authentication http rest client.
     *
     * @param host Host
     * @param port Port
     * @param basePath Base path
     */
    public DefaultHttpAuthRestClient(String host, int port, String basePath) {
        super(host, port, basePath);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
        if(password != null)
            setCredentials();
    }

    /**
     * Define username and password for BASIC authentication.
     *
     * @param user User name
     * @param password Password
     */
    protected void setCredentials() {
        this.getCredentialsProvider().setCredentials(
                new AuthScope(this.getHost(), this.getPort()),
                new UsernamePasswordCredentials(this.user, this.password));
    }
}
