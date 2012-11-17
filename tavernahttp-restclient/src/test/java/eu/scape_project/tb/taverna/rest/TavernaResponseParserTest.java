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

import eu.scape_project.tb.rest.xml.XmlResponseParser;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpException;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import static org.junit.Assert.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Test class of the TavernaResponseParser class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TavernaResponseParserTest {

    private static Logger logger = LoggerFactory.getLogger(TavernaResponseParserTest.class.getName());
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
     * Test of getUUIDfromUUIDResourceURL method, of class TavernaRestUtil.
     */
    @Test
    public void testOutputResponseParser() throws UnsupportedEncodingException, MalformedURLException, IOException {

        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> "
                + "<ns1:workflowOutputs xmlns:ns1=\"http://ns.taverna.org.uk/2010/port/\" "
                + "		xmlns:ns2=\"http://www.w3.org/1999/xlink\" "
                + "		ns1:workflowRunId=\"0caa9d58-224d-4a36-9aea-b7fbe2a8bde2\" "
                + "		ns1:workflowRun=\"http://fue.onb.ac.at/TavernaServer.2.4.1/rest/runs/0caa9d58-224d-4a36-9aea-b7fbe2a8bde2\" "
                + "		ns1:workflowId=\"\"> "
                + "	<ns1:output ns1:depth=\"0\" ns1:name=\"out\"> "
                + "		<ns1:value ns1:contentByteLength=\"67\" "
                + "			ns1:contentType=\"text/plain\" "
                + "			ns1:contentFile=\"/out/out\" "
                + "			ns2:href=\"https://www.google.at/\"/>"
                + "	</ns1:output> "
                + "</ns1:workflowOutputs> ";
        byte[] bytes = test.getBytes("UTF-8");
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
        BasicHttpResponse response = new BasicHttpResponse(statusLine);
        response.setEntity(byteArrayEntity);
        XmlResponseParser responseParser = new XmlResponseParser(response);
        if (responseParser == null) {
            fail("Response parser object not initialised.");
        }
        responseParser.parseResponse();
        assertTrue("Incorrect parsing status.", responseParser.isIsParsed());
        Document doc = responseParser.getResponseXml();
        assertTrue("DOM document does not exist", doc != null);

        String workflowOutputsName = doc.getDocumentElement().getNodeName();
        assertTrue(workflowOutputsName.equals("ns1:workflowOutputs"));

    }

    @Test
    public void testGetRequestsWithXmlResponse() throws MalformedURLException, HttpException {

        TavernaServerRestClient tsrc = new TavernaServerRestClient("http", "localhost", 80, "/TavernaServer.2.4.1/rest");
        tsrc.setUser("taverna");
        tsrc.setPassword("taverna");

        // TODO: dirty test

        String urlStr = "http://fue.onb.ac.at/TavernaServer.2.4.1/rest/runs/cc99dfa1-445d-4a20-93cc-f17b24b40131";

        //String urlStr = tsrc.getBaseUrlStr()+"/runs";
        URL url = new URL(urlStr);
//        List<KeyValuePair> outputValues = tsrc.getWorkflowRunOutputValues(url);
    }
}
