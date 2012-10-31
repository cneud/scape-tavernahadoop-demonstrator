/*
 *  Copyright 2011 SCAPE (www.scape-project.eu)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package eu.scape_project.tb.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class Config {

    private static Logger logger = LoggerFactory.getLogger(Config.class.getName());
    private Properties properties;
    private HashMap<String, String> map;
    
    public static final String CONFIG_PROPERTIES = "/config.properties";

    /**
     * Construct the property utils object from the properties file
     * @param propertiesFile a string path to a properties file
     * @throws GeneratorException
     */
    public Config() {
        try {
            properties = new Properties();
            
            properties.load(Config.class.getResourceAsStream(CONFIG_PROPERTIES));
            logger.debug("Property file \"" + CONFIG_PROPERTIES + "\" loaded.");
        } catch (IOException ex) {
            logger.error("Unable to load properties file!");
        }
    }

    /**
     * @return the properties key value pair map
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> getKeyValuePairs() {
        map = new HashMap<String, String>((Map) properties);
        return map;
    }

    /**
     * get a property value by key
     * @param key the property key
     * @return the property value
     */
    public String getProp(String key) {
        String val = (String) properties.getProperty(key);
        return val;
    }
}
