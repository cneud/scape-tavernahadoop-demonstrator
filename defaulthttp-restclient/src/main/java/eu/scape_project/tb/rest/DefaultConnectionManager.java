/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.tb.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultConnectionManager class for SSL support ("trust everything").
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class DefaultConnectionManager extends BasicClientConnectionManager {

    private static Logger logger = LoggerFactory.getLogger(DefaultConnectionManager.class.getName());
    // Singleton Instance
    private static DefaultConnectionManager instance = null;

    /**
     * Getter for singleton instance
     *
     * @return Singleton instance
     */
    public static DefaultConnectionManager getInstance() {
        if (instance == null) {
            try {
                // SECURE with SSL support (trust everything)
                // SSL certificate validation must be configured, otherwise a
                // "javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated"
                // exception is thrown.
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
                SSLContext.setDefault(ctx);
                // SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER: 
                // Don't check if host name in certificate coincides with the server 
                // name of the Taverna Server, otherwise the exception
                // "javax.net.ssl.SSLException: hostname in certificate didn't match"
                // is thrown.
                SSLSocketFactory sf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SchemeRegistry schemeRegistry = new SchemeRegistry();
                Scheme httpsScheme = new Scheme("https", 8443, sf);
                schemeRegistry.register(httpsScheme);
                BasicClientConnectionManager cm = new BasicClientConnectionManager(schemeRegistry);
                instance = new DefaultConnectionManager(schemeRegistry);
            } catch (KeyManagementException ex) {
                logger.error("Key Management Exception error", ex);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("No Such Algorithm error", ex);
            }

        }
        return instance;
    }

    private DefaultConnectionManager(SchemeRegistry schemeRegistry) {
        super(schemeRegistry);
    }
}
