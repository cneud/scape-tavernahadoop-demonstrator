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
package eu.scape_project.tb.taverna;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException; 
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

/**
 * Test class for T2Flow class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class T2FlowFileTest {
    
    private static File workflowTestFile;
    
    public T2FlowFileTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
            InputStream workflowResource = T2FlowFileTest.class.getResourceAsStream("minimal_tool.t2flow");
            workflowTestFile = File.createTempFile("minimal_tool", ".t2flow");
            FileOutputStream fos = new FileOutputStream(workflowTestFile);
            IOUtils.copyLarge(workflowResource, fos);
            workflowTestFile.deleteOnExit();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws IOException {
        
            
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getFirstWorkflowInputPorts method, of class T2FlowFile.
     */
    @Test
    public void testGetFirstWorkflowInputPorts() {
        System.out.println("getFirstWorkflowOutputPorts");
        T2FlowFile instance = new T2FlowFile(workflowTestFile.getAbsolutePath());
        Set result = instance.getFirstWorkflowInputPorts();
        Iterator it = result.iterator();
        InputWorkflowPort owp = (InputWorkflowPort) it.next();
        assertEquals("in", owp.getName());
    }

    /**
     * Test of getFirstWorkflowOutputPorts method, of class T2FlowFile.
     */
    @Test
    public void testGetFirstWorkflowOutputPorts() {
        System.out.println("getFirstWorkflowOutputPorts");
        T2FlowFile instance = new T2FlowFile(workflowTestFile.getAbsolutePath());
        Set result = instance.getFirstWorkflowOutputPorts();
        Iterator it = result.iterator();
        OutputWorkflowPort owp = (OutputWorkflowPort) it.next();
        assertEquals("out", owp.getName());
    }

    /**
     * Test of getFirstWorkflow method, of class T2FlowFile.
     */
    @Test
    public void testGetFirstWorkflow() {
        System.out.println("getFirstWorkflow");
        T2FlowFile instance = new T2FlowFile(workflowTestFile.getAbsolutePath());
        String expResult = "Minimal1";
        Workflow result = instance.getFirstWorkflow();
        assertEquals(expResult, result.getName());
    }
}
