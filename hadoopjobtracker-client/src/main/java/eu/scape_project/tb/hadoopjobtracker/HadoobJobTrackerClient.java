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
package eu.scape_project.tb.hadoopjobtracker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.mapred.*;

/**
 *
 * @author onbscs
 */
public class HadoobJobTrackerClient {

    private static Logger logger = LoggerFactory.getLogger(HadoobJobTrackerClient.class.getName());
    JobClient myJobClient;

    public HadoobJobTrackerClient(String jobTrackerHostName, int jobTrackerHostPort) {

        logger.info("jobTrackerHostName: " + jobTrackerHostName + "; jobTrackerHostPort: " + jobTrackerHostPort);

        Configuration conf = new Configuration();

        try {
            myJobClient = new JobClient(new InetSocketAddress(jobTrackerHostName, jobTrackerHostPort), conf);
            myJobClient.setConf(conf);
        } catch (IOException ex) {
            logger.error("Error connecting to the JobTracker. ERR: " + ex.getMessage());
        }

    }

    public HDClusterStatus ClusterStatus() {
        HDClusterStatus clusterStatus = new HDClusterStatus();
        try {
            ClusterStatus currentClusterStatus = myJobClient.getClusterStatus(true);

            String jobTrackerStatus = currentClusterStatus.getJobTrackerStatus().toString();
            int availableTaskTrackers = currentClusterStatus.getTaskTrackers();
            int blackTaskTrackers = currentClusterStatus.getBlacklistedTrackers();
            int maxMapTasks = currentClusterStatus.getMaxMapTasks();
            int maxReduceTasks = currentClusterStatus.getMaxReduceTasks();

            logger.info("JobTrackerState: " + jobTrackerStatus);
            logger.info("Active TaskTrackers: " + availableTaskTrackers);
            logger.info("BlackListed TaskTrackers: " + blackTaskTrackers);
            logger.info("Max MAP Tasks: " + maxMapTasks);
            logger.info("Max REDUCE Tasks: " + maxReduceTasks);

            clusterStatus.setJobTrackerState(jobTrackerStatus);
            clusterStatus.setTaskTrackersAvailable(availableTaskTrackers);
            clusterStatus.setTaskTrackersBlacklisted(blackTaskTrackers);
            clusterStatus.setMaxMapTaskSlots(maxMapTasks);
            clusterStatus.setMaxReduceTaskSlots(maxReduceTasks);

        } catch (IOException ex) {
            logger.error("Error filling HDClusterStatus. ERR: " + ex.getMessage());
        }

        return clusterStatus;
    }
//
//    public HDJobStatus[] AllJobs() {
//
//        JobStatus[] allHadoopJobs = null;
//
//        try {
//            allHadoopJobs = myJobClient.getAllJobs();
//        } catch (IOException ex) {
//            logger.error("Error retrieving ALL jobs from JobTracker. ERR: " + ex.getMessage());
//        }
//
//        int numberOfJobs = allHadoopJobs.length;
//        logger.info("Number of ALL jobs on the cluster: " + numberOfJobs);
//
//        HDJobStatus[] allCltJobs = new HDJobStatus[numberOfJobs];
//
//
//        if (numberOfJobs > 0) {
//            int count = 0;
//            for (JobStatus singleJobStatus : allHadoopJobs) {
//                JobID singleJobID = singleJobStatus.getJobID();
//                String singleJobName = "N/A";
//                try {
//                    singleJobName = getJobName(myJobClient, singleJobID);
//                } catch (IOException ex) {
//                    logger.error("Error retrieving jobName for job " + singleJobID + ". ERR: " + ex.getMessage());
//                }
//
//                allCltJobs[count] = new HDJobStatus();
//                allCltJobs[count].setJobID(singleJobStatus.getJobID().toString());
//                allCltJobs[count].setJobIsComplete(singleJobStatus.isJobComplete());
//                allCltJobs[count].setJobUserName(singleJobStatus.getUsername());
//                allCltJobs[count].setJobFailureInfo(singleJobStatus.getFailureInfo());
//                allCltJobs[count].setJobName(singleJobName);
//                allCltJobs[count].setJobMapProgress(Math.round(singleJobStatus.mapProgress() * 100));
//                allCltJobs[count].setJobReduceProgress(Math.round(singleJobStatus.reduceProgress() * 100));
//                count++;
//            }
//        }
//        return allCltJobs;
//    }

    public HDJobStatus[] UUIDJobs(String UUID) {

        JobStatus[] allHadoopJobs = null;

        try {
            allHadoopJobs = myJobClient.getAllJobs();
        } catch (IOException ex) {
            logger.error("Error retrieving ALL jobs from JobTracker. ERR: " + ex.getMessage());
        }

        int numberOfJobs = allHadoopJobs.length;
        logger.info("Number of ALL jobs on the cluster: " + numberOfJobs);
        logger.info("Searching for jobs containing the UUID: '" + UUID + "'");

        ArrayList<HDJobStatus> allUUIDJobs = new ArrayList<HDJobStatus>();
        if (numberOfJobs > 0) {
            for (JobStatus singleJobStatus : allHadoopJobs) {

                JobID singleJobID = singleJobStatus.getJobID();
                String singleJobName = "N/A";
                try {
                    singleJobName = getJobName(myJobClient, singleJobID);
                } catch (IOException ex) {
                    logger.error("Error retrieving jobName for job " + singleJobID + ". ERR: " + ex.getMessage());
                }
                
                if ((singleJobName.toLowerCase().indexOf(UUID.toLowerCase()) >= 0) || (UUID.equals(""))) {
                    HDJobStatus newJobStatus = new HDJobStatus();
                    newJobStatus.setJobID(singleJobStatus.getJobID().toString());
                    newJobStatus.setJobIsComplete(singleJobStatus.isJobComplete());
                    newJobStatus.setJobUserName(singleJobStatus.getUsername());
                    newJobStatus.setJobFailureInfo(singleJobStatus.getFailureInfo());
                    newJobStatus.setJobName(singleJobName);
                    newJobStatus.setJobMapProgress(Math.round(singleJobStatus.mapProgress() * 100));
                    newJobStatus.setJobReduceProgress(Math.round(singleJobStatus.reduceProgress() * 100));
                    allUUIDJobs.add(newJobStatus);
                }
            }
        }
        return allUUIDJobs.toArray(new HDJobStatus[allUUIDJobs.size()]);
    }

    private static String getJobName(JobClient jobClt, JobID singleJobID) throws IOException {
        RunningJob runJob = jobClt.getJob(singleJobID);
        String runJobName = runJob.getJobName();
        return runJobName;
    }
}
