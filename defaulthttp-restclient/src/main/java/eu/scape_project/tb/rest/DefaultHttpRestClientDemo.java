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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the simple http rest client. This client provides a simple http
 * rest client with a small set of REST request methods defined in the
 * DefaultHttpRestClient class. It also includes a basic xml parser for
 * processing an XML response.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class DefaultHttpRestClientDemo {

    private static Logger logger = LoggerFactory.getLogger(DefaultHttpRestClientDemo.class.getName());

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * Main method of the simple http rest client.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {

//        SSLContext sslContext = SSLContext.getInstance("SSL");
//        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    System.out.println("getAcceptedIssuers =============");
//                    return null;
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] certs,
//                        String authType) {
//                    System.out.println("checkClientTrusted =============");
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] certs,
//                        String authType) {
//                    System.out.println("checkServerTrusted =============");
//                }
//            }}, new SecureRandom());
//
//        SSLSocketFactory sf = new SSLSocketFactory(sslContext);
//        Scheme httpsScheme = new Scheme("https", 8443, sf);
//        Scheme httpScheme = new Scheme("http", 8080, sf);
//        SchemeRegistry schemeRegistry = new SchemeRegistry();
//        schemeRegistry.register(httpsScheme);
//        schemeRegistry.register(httpScheme);
//        BasicClientConnectionManager cm = new BasicClientConnectionManager(schemeRegistry);
        
        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        SSLContext ctx = SSLContext.getInstance("TLS");
        
        ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);
        
        SSLSocketFactory sf = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        //sf.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        Scheme httpsScheme = new Scheme("https", 8443, sf);
        Scheme httpScheme = new Scheme("http", 8080, sf);
        
        
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);
        schemeRegistry.register(httpScheme);
        BasicClientConnectionManager cm = new BasicClientConnectionManager(schemeRegistry);

        
        // Example: Google search REST API
        // http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=Google%20REST%20API
        DefaultHttpAuthRestClient simpleRestClient = new DefaultHttpAuthRestClient(cm,"fue-hdc01", 8443, "/TavernaServer.2.4.1/rest");
        simpleRestClient.setUser("taverna");
        simpleRestClient.setPassword("taverna");
        simpleRestClient.executeGet("runs", "application/xml");
    }
}
