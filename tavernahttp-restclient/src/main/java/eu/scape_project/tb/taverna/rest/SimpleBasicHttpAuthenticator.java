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

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for direct access to the Taverna server.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class SimpleBasicHttpAuthenticator extends Authenticator {

    private static Logger logger = LoggerFactory.getLogger(SimpleBasicHttpAuthenticator.class.getName());
    String username = "taverna";
    String password = "taverna";

    private SimpleBasicHttpAuthenticator() {
    }

    public SimpleBasicHttpAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }
}
