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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 * Test class of the XmlResponseUnmarshaller class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class XmlResponseUnmarshallerTest {
    
    public XmlResponseUnmarshallerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of unmarshall method, of class XmlResponseUnmarshaller.
     */
    @Test
    public void testUnmarshall() throws Exception {
        System.out.println("unmarshall");
        InputStream wfRunXmlResponseIs = XmlResponseUnmarshallerTest.class.getResourceAsStream("workflowrun.xml");
        RunDescription result = XmlResponseUnmarshaller.unmarshall(wfRunXmlResponseIs);
        assertNotNull(result); 
        assertEquals(result.getOwner(),"taverna");
        assertEquals(result.getExpiry().get(0).getHref(),
                "https://localhost:8443/TavernaServer.2.4.1/rest/runs/c382adb2-fd72-4acc-9b4a-94acec8c54ac/expiry");
        assertEquals(result.getCreationWorkflow().get(0).getHref(),
                "https://localhost:8443/TavernaServer.2.4.1/rest/runs/c382adb2-fd72-4acc-9b4a-94acec8c54ac/workflow");
        assertEquals(result.getCreateTime().get(0).getHref(),
                "https://localhost:8443/TavernaServer.2.4.1/rest/runs/c382adb2-fd72-4acc-9b4a-94acec8c54ac/createTime");
        assertEquals(result.getStartTime().get(0).getHref(),
                "https://localhost:8443/TavernaServer.2.4.1/rest/runs/c382adb2-fd72-4acc-9b4a-94acec8c54ac/startTime");
        assertEquals(result.getFinishTime().get(0).getHref(),
                "https://localhost:8443/TavernaServer.2.4.1/rest/runs/c382adb2-fd72-4acc-9b4a-94acec8c54ac/finishTime");
        assertEquals(result.getStatus().get(0).getHref(),
                "https://localhost:8443/TavernaServer.2.4.1/rest/runs/c382adb2-fd72-4acc-9b4a-94acec8c54ac/status");
        
    }
}
