/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.tb.config;

import eu.scape_project.tb.config.TavernaConfig;
import java.io.File;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests of the TavernaConfig class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TavernaConfigTest {
    
    private static Logger logger = LoggerFactory.getLogger(TavernaConfigTest.class.getName());
    
    public TavernaConfigTest() {
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
     * Test of getProp method, of class TavernaConfig.
     */
    @Test
    public void testConfiguration() {
        
        System.out.println("getProp");
        TavernaConfig config = new TavernaConfig();
        
        String key = "taverna.server.port";
        String result = config.getProp(key);
        // not empty
        assertTrue("Port config missing",result != null && !result.equals(""));
        // should be a number, test fails with exception otherwise
        Integer.parseInt(result);
        
        key = "taverna.server.host";
        result = config.getProp(key);
        // not empty
        assertTrue("host config missing",result != null  && !result.equals(""));
        
        key = "taverna.server.username";
        result = config.getProp(key);
        // not empty
        assertTrue("taverna username config missing",result != null  && !result.equals(""));
        
        key = "taverna.server.password";
        result = config.getProp(key);
        // not empty
        assertTrue("taverna password config missing",result != null  && !result.equals(""));
        
        key = "taverna.server.restapi.basepath";
        result = config.getProp(key);
        // not empty
        assertTrue("taverna server restapi basepath config missing",result != null  && !result.equals(""));
        
        key = "taverna.workflow.upload.path";
        result = config.getProp(key);
        // not empty
        assertTrue("taverna workflow upload path config missing",result != null  && !result.equals(""));
        // directory exists and is writable?
        File dir = new File (result);
        assertTrue("taverna workflow upload directory does not exist",dir.exists());
        assertTrue("The taverna workflow upload path is not a directory",dir.isDirectory());
        assertTrue("Unable to write to taverna workflow upload directory",dir.canWrite());
        assertTrue("Trailing slash in taverna workflow upload path missing",result.lastIndexOf(File.separator) == result.length()-1);
    }
}
