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

import junit.framework.TestSuite;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit tests of the DefaultHttpRestClient class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class SimpleHttpRestClientTest extends TestSuite {

    private static Logger logger = LoggerFactory.getLogger(SimpleHttpRestClientTest.class.getName());

    private DefaultHttpRestClient client;
    
    /**
     * Constructor.
     */
    public SimpleHttpRestClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Set up.
     */
    @Before
    public void setUp() {
        client = new DefaultHttpRestClient("ajax.googleapis.com", 80, "/ajax/services/search");
    }

    /**
     * Test of the getBaseUrlStr method.
     */
    @Test
    public void testGetBaseUrlStr() {
        String result = client.getBaseUrlStr();
        String expected = "http://ajax.googleapis.com:80/ajax/services/search";
        assertTrue("Incorrect base URL",result.equals(expected));
    }

}
