/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.tb.taverna.rest;

import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author onbscs
 */
public class TavernaRestUtilTest {
    
    public TavernaRestUtilTest() {
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
     * Test of getUUIDfromUUIDResourceURL method, of class TavernaRestUtil.
     */
    @Test
    public void testGetUUIDfromUUIDResourceURL() {
        
        System.out.println("getUUIDfromUUIDResourceURL1");
        String uuidResourceURL1 = "http://fue-l:8080/tavernaserver/rest/runs/euydks6288sksldi728";
        String expResult1 = "euydks6288sksldi728";
        String result1 = TavernaRestUtil.getUUIDfromUUIDResourceURL(uuidResourceURL1);
        assertEquals(expResult1, result1);
        
        System.out.println("getUUIDfromUUIDResourceURL2");
        String uuidResourceURL2 = "http://fue-l:8080/tavernaserver/rest/runs/euydks6288sksldi728/some";
        String expResult2 = "euydks6288sksldi728";
        String result2 = TavernaRestUtil.getUUIDfromUUIDResourceURL(uuidResourceURL2);
        assertEquals(expResult2, result2);
        
        System.out.println("getUUIDfromUUIDResourceURL3");
        String uuidResourceURL3 = "http://fue-l:8080/tavernaserver/rest/runs/euydks6288sksldi728/some/other";
        String expResult3 = "euydks6288sksldi728";
        String result3 = TavernaRestUtil.getUUIDfromUUIDResourceURL(uuidResourceURL3);
        assertEquals(expResult3, result3);
        
        System.out.println("getUUIDfromUUIDResourceURL4");
        String uuidResourceURL4 = "http://fue-l:8080/tavernaserver/rest/runs/euydks6288sksldi728/some/";
        String expResult4 = "euydks6288sksldi728";
        String result4 = TavernaRestUtil.getUUIDfromUUIDResourceURL(uuidResourceURL4);
        assertEquals(expResult4, result4);
        
    }
}
