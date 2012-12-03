/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.scape_project.tb.taverna.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TavernaRestUtil {

    private static Logger logger = LoggerFactory.getLogger(TavernaRestUtil.class.getName());
    
    public static String getUUIDfromUUIDResourceURL(String uuidResourceURL) {
        String uuid = "";
        // http://fue-l:8080/tavernaserver/rest/runs/euydks6288sksldi728
        int start = uuidResourceURL.indexOf("/rest/runs/");
        start += 11;
        uuid = uuidResourceURL.substring(start);
        while(uuid.contains("/")) {
            uuid = uuid.substring(0,uuid.lastIndexOf("/"));
        }
        return uuid;
    }
    
}
