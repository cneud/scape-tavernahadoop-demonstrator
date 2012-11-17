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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple HTTP Rest client. This client provides a simple http rest client with
 * a small set of REST request methods.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class DefaultHttpRestClient extends DefaultHttpClient {

    private String scheme;
    private String host;
    private int port;
    private String basePath;
    protected URI baseUri;
    private static Logger logger = LoggerFactory.getLogger(DefaultHttpRestClient.class.getName());
    
    protected HttpContext httpContext;

    /**
     * Constructor of the simple authentication http rest client (Insecure).
     *
     * @param host Host
     * @param port Port
     * @param basePath Base path
     */
    public DefaultHttpRestClient(String scheme, String host, int port, String basePath) {
        init(scheme, host, port, basePath);
    }

    /**
     * Constructor of the simple authentication http rest client (Secure).
     *
     * @param host Host
     * @param port Port
     * @param basePath Base path
     */
    public DefaultHttpRestClient(BasicClientConnectionManager bccm, String scheme, String host, int port, String basePath) {
        super(bccm);
        init(scheme, host, port, basePath);
    }

    private void init(String scheme, String host, int port, String basePath) {
        httpContext = new BasicHttpContext();
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.basePath = basePath;
        try {
            baseUri = new URIBuilder().setScheme(scheme).setHost(host).setPort(port).setPath(this.basePath).build();
        } catch (URISyntaxException ex) {
            logger.error("URI Syntax Error", ex);
        }
    }

    /**
     * Unused empty constructor.
     */
    private DefaultHttpRestClient() {
    }

    /**
     * Execute GET request. Response entity must be consumed by the caller.
     *
     * @param resourceUrl Sub-resource is added to the base path
     * @return HTTP response
     */
    public HttpResponse executeGet(URL resourceUrl, String headerAccept) {
        HttpGet httpget = new HttpGet(resourceUrl.toExternalForm());
        httpget.addHeader("Accept", headerAccept);

        logger.info("Resource path: " + resourceUrl.toExternalForm());
        HttpResponse response = null;
        try {
            logger.info("executing request " + httpget.getRequestLine());
            response = this.execute(httpget, httpContext);
            Header contentTypeHeader = response.getEntity().getContentType();
            logger.info("content type: " + contentTypeHeader.toString());
            HttpEntity entity = response.getEntity();
            logger.info(response.getStatusLine().toString());
            long contLen = entity.getContentLength();
            if (entity != null) {
                logger.info("HTTP GET response content length: " + contLen);
            }
        } catch (IOException e) {
            logger.error("I/O Error while executing HTTP GET request", e);
        }
        return response;
    }

    /**
     * Execute GET request. Response entity must be consumed by the caller.
     *
     * @param subResource Sub-resource is added to the base path
     * @return HTTP response
     */
    public HttpResponse executeGet(String subResource, String headerAccept) {
        String resource = FileUtility.makePath(this.getBaseUrlStr(), subResource);
        URL resourceUrl = null;
        try {
            resourceUrl = new URL(resource);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error while executing HTTP GET request", ex);
        }
        return executeGet(resourceUrl, headerAccept);
    }

    /**
     * Execute GET request. Response entity must be consumed by the caller.
     *
     * @param subResource Sub-resource is added to the base path
     * @return HTTP response
     */
    public HttpResponse executePut(URL resourceUrl, String putContent, ContentType contentType) {
        HttpPut httpPut = new HttpPut(resourceUrl.toExternalForm());
        HttpResponse response = null;
        try {
            StringEntity stringEntity = new StringEntity(putContent, "UTF-8");
            BasicHeader contentTypeHeader = new BasicHeader("Content-Type", contentType.getMimeType());
            stringEntity.setContentType(contentTypeHeader);
            httpPut.setEntity(stringEntity);
            logger.info("executing request " + httpPut.getRequestLine()
                    + "with value:\n" + putContent + "\nand mime type:\n"
                    + contentType.toString() + "");
            response = this.execute(httpPut, httpContext);
            HttpEntity entity = response.getEntity();
            logger.info(response.getStatusLine().toString());
            if (entity != null) {
                logger.info("HTTP PUT response content length: " + entity.getContentLength());
            }
        } catch (IOException e) {
            logger.error("I/O Error while executing HTTP PUT request", e);
        }
        return response;
    }

    /**
     * Execute GET request. Response entity must be consumed by the caller.
     *
     * @param subResource Sub-resource is added to the base path
     * @return HTTP response
     */
    public HttpResponse executePut(String subResource, String putContent, ContentType contentType) {
        String resource = FileUtility.makePath(this.getBaseUrlStr(), subResource);
        URL resourceUrl = null;
        try {
            resourceUrl = new URL(resource);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error while executing HTTP PUT request", ex);
        }
        return executePut(resourceUrl, putContent, contentType);
    }

    /**
     * Execute POST request. Response entity must be consumed by the caller.
     *
     * @param subResource Sub-resource is added to the base path
     * @param file File that is posted.
     * @param contentType Content type of the file
     * @return HTTP response
     */
    public HttpResponse executeFileContentPost(String subResource, File file, ContentType contentType) {
        String resource = FileUtility.makePath(this.getBaseUrlStr(), subResource);
        HttpPost httpPost = new HttpPost(resource);
        HttpResponse response = null;
        try {
            FileEntity fileEntity = new FileEntity(file, contentType);
            httpPost.setEntity(fileEntity);
            response = this.execute(httpPost, httpContext);
            HttpEntity entity = response.getEntity();
            logger.info(response.getStatusLine().toString());
            if (entity != null) {
                logger.info("HTTP POST response content length: " + entity.getContentLength());
            }
        } catch (IOException e) {
            logger.error("IOException occurred.", e);
        }
        return response;
    }

    /**
     * Execute DELETE request. Response entity must be consumed by the caller.
     *
     * @param subResource Sub-resource is added to the base path
     * @return HTTP response
     */
    public HttpResponse executeDelete(URL resourceUrl) {
        HttpDelete httpDelete = new HttpDelete(resourceUrl.toExternalForm());
        HttpResponse response = null;
        try {
            logger.info("executing request " + httpDelete.getRequestLine());
            response = this.execute(httpDelete, httpContext);
            HttpEntity entity = response.getEntity();
            logger.info(response.getStatusLine().toString());
            if (entity != null) {
                logger.info("HTTP DELETE response content length: " + entity.getContentLength());
            }
        } catch (IOException e) {
            logger.error("I/O Error while executing HTTP DELETE request");
        }
        return response;
    }

    /**
     * Execute DELETE request. Response entity must be consumed by the caller.
     *
     * @param uuid Sub-resource is added to the base path
     * @return HTTP response
     */
    public HttpResponse executeDelete(String uuid) {
        String resource = FileUtility.makePath(this.getBaseUrlStr(), "rest/runs", uuid);
        URL resourceUrl = null;
        try {
            resourceUrl = new URL(resource);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL Error while executing HTTP DELETE request", ex);
        }
        return executeDelete(resourceUrl);
    }

    /**
     * Getter of the host field
     *
     * @return host field value
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter of the port field
     *
     * @return port field value
     */
    public int getPort() {
        return port;
    }

    public String getScheme() {
        return scheme;
    }

    /**
     * Get full base URL. Full base URL is the combination of the scheme, host,
     * port, and base path information.
     *
     * @return baseUrl field value
     */
    public String getBaseUrlStr() {
        String urlStr = baseUri.getScheme() + "://" + baseUri.getHost() + ":"
                + baseUri.getPort() + baseUri.getPath();
        return urlStr;
    }

    protected void throwHttpError(String msg) throws HttpException {
        logger.error(msg);
        throw new HttpException(msg);
    }

    protected void consume(HttpResponse response) {
        if (response != null) {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                try {
                    EntityUtils.consume(httpEntity);
                } catch (IOException ex) {
                    logger.error("Error while consuming response entity content.", ex);
                }
            }
        }
    }
}
