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

import eu.scape_project.tb.rest.util.FileUtility;
import java.io.File;
import junit.framework.TestSuite;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit tests of the FileUtility class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class FileUtilityTest extends TestSuite {

    private static Logger logger = LoggerFactory.getLogger(FileUtilityTest.class.getName());

    /**
     * Constructor.
     */
    public FileUtilityTest() {
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

    }

    /**
     * Test of the makeRestPath method.
     */
    @Test
    public void testMakeRestPath() {
        
        String result = FileUtility.makePath("one", "two");
        String expected = "/one"+File.separator+"two";
        logger.info("Result path for one, two: "+result);
        assertTrue("Incorrect path composition",result.equals(expected));

        result = FileUtility.makePath("one/", "two");
        expected = "/one"+File.separator+"two";
        logger.info("Result path for one/, two: "+result);
        assertTrue("Incorrect path composition",result.equals(expected));
        
        result = FileUtility.makePath("one/", "two/");
        expected = "/one"+File.separator+"two";
        logger.info("Result path for one/, two/: "+result);
        assertTrue("Incorrect path composition",result.equals(expected));
       
        result = FileUtility.makePath("/one", "two");
        expected = "/one"+File.separator+"two";
        logger.info("Result path for /one, two: "+result);
        assertTrue("Incorrect path composition",result.equals(expected));
        
        result = FileUtility.makePath("one", "/two");
        expected = "/one"+File.separator+"two";
        logger.info("Result path for one, /two: "+result);
        assertTrue("Incorrect path composition",result.equals(expected));
        
        result = FileUtility.makePath("one/two/", "/three");
        expected = "/one/two"+File.separator+"three";
        logger.info("Result path for one, /two: "+result);
        assertTrue("Incorrect path composition",result.equals(expected));
        
        result = FileUtility.makePath("http://fue-hdc01:8080/TavernaServer.2.4.1/rest", "runs");
        expected = "http://fue-hdc01:8080/TavernaServer.2.4.1/rest/runs";
        logger.info("Result for http://fue-hdc01:8080/TavernaServer.2.4.1/rest,runs: "+result);
        assertTrue("Incorrect path composition",result.equals(expected));
    }

}
