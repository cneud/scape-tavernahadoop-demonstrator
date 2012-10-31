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

import java.io.UnsupportedEncodingException;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * JUnit tests of the XPathEvaluatorTest class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class XPathEvaluatorTest {

    private BasicStatusLine statusLine;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        ProtocolVersion pv = new ProtocolVersion("1.0", 1, 1);
        statusLine = new BasicStatusLine(pv, 200, "Accepted");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of evaluate method, of class XPathEvaluator.
     */
    @Test
    public void testParseResponse() throws UnsupportedEncodingException {
        String test = "<?xml version=\"1.0\"?><response>message</response>";
        byte[] bytes = test.getBytes("UTF-8");
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
        BasicHttpResponse response = new BasicHttpResponse(statusLine);
        response.setEntity(byteArrayEntity);
        ResponseParser responseParser = new ResponseParser(response);
        if (responseParser == null) {
            fail("Response parser object not initialised.");
        }
        responseParser.parseResponse();
        assertTrue("Incorrect parsing status.", responseParser.isIsParsed());
        Document doc = responseParser.getResponseXml();
        String responseElementName = doc.getDocumentElement().getNodeName();
        assertTrue("DOM element not accessible", responseElementName.equals("response"));
        XPathEvaluator xPathEval = new XPathEvaluator(responseParser);
        NodeList nl = xPathEval.evaluate("/response");
        assertTrue("Incorrect node list",nl.getLength()==1);
    }
}
