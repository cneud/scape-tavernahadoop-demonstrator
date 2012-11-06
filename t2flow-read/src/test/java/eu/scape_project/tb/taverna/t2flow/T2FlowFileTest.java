/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.tb.taverna.t2flow;

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
 *
 * @author onbscs
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
