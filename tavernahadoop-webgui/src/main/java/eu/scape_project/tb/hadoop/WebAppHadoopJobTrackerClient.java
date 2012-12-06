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
package eu.scape_project.tb.hadoop;

import eu.scape_project.tb.config.HadoopConfig;
import eu.scape_project.tb.hadoopjobtracker.HDJobStatus;
import eu.scape_project.tb.hadoopjobtracker.HadoobJobTrackerClient;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web application job tracker client.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class WebAppHadoopJobTrackerClient {
    
    private static Logger logger = LoggerFactory.getLogger(WebAppHadoopJobTrackerClient.class.getName());
    private HadoopConfig config;
    private HadoobJobTrackerClient myJobClt;
    // Singleton Instance
    private static WebAppHadoopJobTrackerClient instance = null;
    
    public static WebAppHadoopJobTrackerClient getInstance() {
        if (instance == null) {
            instance = new WebAppHadoopJobTrackerClient();
        }
        return instance;
    }
    
    private WebAppHadoopJobTrackerClient() {
        config = new HadoopConfig();
        
        String hadoopJobTrackerHost = config.getProp("hadoop.jobtracker.host");
        logger.info("Hadoop job tracker host configuration: " + hadoopJobTrackerHost);
        int hadoopJobTrackerPort = 0;
        try {
            hadoopJobTrackerPort = Integer.parseInt(config.getProp("hadoop.jobtracker.port"));
        } catch (java.lang.NumberFormatException ex) {
            logger.error("Invalid hadoop job tracker port number configuration.");
        }
        logger.info("Hadoop job tracker host: " + hadoopJobTrackerPort);
        myJobClt = new HadoobJobTrackerClient(hadoopJobTrackerHost, hadoopJobTrackerPort);
    }
    
    public List<HDJobStatus> getHadoopJobs(String uuid) {
        List<HDJobStatus> hdJobs = new ArrayList<HDJobStatus>();
        if (instance == null) {
            instance = new WebAppHadoopJobTrackerClient();
        }
        //Job UUID (if this is part of the JobName the jobObject will go into the HDJobStatus[])
        HDJobStatus[] uuidCltJobs = myJobClt.UUIDJobs(uuid);
        if (uuidCltJobs.length > 0) {
            for (HDJobStatus singleUUIDJobStatus : uuidCltJobs) {
                hdJobs.add(singleUUIDJobStatus);
                logger.debug("JobID: " + singleUUIDJobStatus.getJobID());
                logger.debug("    JobName: " + singleUUIDJobStatus.getJobName());
                logger.debug("    Is complete: " + singleUUIDJobStatus.getJobIsComplete());
                logger.debug("    User: " + singleUUIDJobStatus.getJobUserName());
                logger.debug("    Failure info: " + singleUUIDJobStatus.getJobFailureInfo());
                logger.debug("    Map progress: " + singleUUIDJobStatus.getJobMapProgress());
                logger.debug("    Reduce progress: " + singleUUIDJobStatus.getJobReduceProgress());
            }
        } else {
            logger.info("No jobs found for uuid " + uuid);
        }
        return hdJobs;
    }
}
