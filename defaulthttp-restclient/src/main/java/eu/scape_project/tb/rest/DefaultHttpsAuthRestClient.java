package eu.scape_project.tb.rest;


import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class DefaultHttpsAuthRestClient extends DefaultHttpAuthRestClient {

    private static Logger logger = LoggerFactory.getLogger(DefaultHttpsAuthRestClient.class.getName());
    
    public DefaultHttpsAuthRestClient(BasicClientConnectionManager bccm, String scheme, String host, int port, String basePath) {
        super(bccm);
        init(scheme, host, port, basePath);
    }

}
