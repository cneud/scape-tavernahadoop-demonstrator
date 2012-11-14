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

import org.apache.hadoop.mapred.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author onbram
 */
public class HadoobJobTrackerClientDemo {

    private static Logger logger = LoggerFactory.getLogger(HadoobJobTrackerClientDemo.class.getName());

    public static void main(String[] args) {

        String myJobTrackerName = "localhost";
        //String myJobTrackerName = "fue-hdc01";
        int myJobTrackerPort = 8021;

        HadoobJobTrackerClient myJobClt = new HadoobJobTrackerClient(myJobTrackerName, myJobTrackerPort);


        System.out.println("***Get the cluster status object.");
        HDClusterStatus ClusterStatus = myJobClt.ClusterStatus();
        System.out.println("JobTrackerState: " + ClusterStatus.getJobTrackerState());
        System.out.println("Active TaskTrackers: " + ClusterStatus.getTaskTrackersAvailable());
        System.out.println("BlackListed TaskTrackers: " + ClusterStatus.getTaskTrackersBlacklisted());
        System.out.println("Max MAP Tasks: " + ClusterStatus.getMaxMapTaskSlots());
        System.out.println("Max REDUCE Tasks: " + ClusterStatus.getMaxReduceTaskSlots());
        System.out.println("**************************");

//        System.out.println("***Get a list of ALL jobs.");
//            HDJobStatus[] allCltJobs = myJobClt.AllJobs();
//            for (HDJobStatus singleJobStatus : allCltJobs) {
//                System.out.println("JobID: " + singleJobStatus.getJobID());
//                System.out.println("    JobName: " + singleJobStatus.getJobName());
//                System.out.println("    Is complete: " + singleJobStatus.getJobIsComplete());
//                System.out.println("    User: " + singleJobStatus.getJobUserName());
//                System.out.println("    Failure info: " + singleJobStatus.getJobFailureInfo());
//                System.out.println("    Map progress: " + singleJobStatus.getJobMapProgress());
//                System.out.println("    Reduce progress: " + singleJobStatus.getJobReduceProgress());
//            }
//        System.out.println("**************************");


        System.out.println("***Get a list of ALL jobs");
        String UUIDall = "";   //This will return all jobs
        HDJobStatus[] uuidCltJobsAll = myJobClt.UUIDJobs(UUIDall);
        for (HDJobStatus singleUUIDJobStatusAll : uuidCltJobsAll) {
            System.out.println("JobID: " + singleUUIDJobStatusAll.getJobID());
            System.out.println("    JobName: " + singleUUIDJobStatusAll.getJobName());
            System.out.println("    Is complete: " + singleUUIDJobStatusAll.getJobIsComplete());
            System.out.println("    User: " + singleUUIDJobStatusAll.getJobUserName());
            System.out.println("    Failure info: " + singleUUIDJobStatusAll.getJobFailureInfo());
            System.out.println("    Map progress: " + singleUUIDJobStatusAll.getJobMapProgress());
            System.out.println("    Reduce progress: " + singleUUIDJobStatusAll.getJobReduceProgress());
        }
        System.out.println("**************************");


        System.out.println("***Get a list of jobs with a specific UUID.");
        String UUID = "c009b143-68ed-47c5-ae22-8c1d9a00544c";   //Job UUID (if this is part of the JobName the jobObject will go into the HDJobStatus[])
        HDJobStatus[] uuidCltJobs = myJobClt.UUIDJobs(UUID);
        for (HDJobStatus singleUUIDJobStatus : uuidCltJobs) {
            System.out.println("JobID: " + singleUUIDJobStatus.getJobID());
            System.out.println("    JobName: " + singleUUIDJobStatus.getJobName());
            System.out.println("    Is complete: " + singleUUIDJobStatus.getJobIsComplete());
            System.out.println("    User: " + singleUUIDJobStatus.getJobUserName());
            System.out.println("    Failure info: " + singleUUIDJobStatus.getJobFailureInfo());
            System.out.println("    Map progress: " + singleUUIDJobStatus.getJobMapProgress());
            System.out.println("    Reduce progress: " + singleUUIDJobStatus.getJobReduceProgress());
        }
        System.out.println("**************************");
    }
}
