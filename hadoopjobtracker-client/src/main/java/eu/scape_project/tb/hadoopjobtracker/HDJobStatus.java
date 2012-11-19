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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author onbram
 */
public class HDJobStatus {
    
    private static Logger logger = LoggerFactory.getLogger(HDJobStatus.class.getName());
    
    private String JobName;
    private String JobID;
    private String JobUserName;
    private String JobFailureInfo;
    private boolean JobIsComplete;
    private int JobMapProgress;
    private int JobReduceProgress;

    /**
     * @return the JobName
     */
    public String getJobName() {
        return JobName;
    }

    /**
     * @param JobName the JobName to set
     */
    public void setJobName(String JobName) {
        this.JobName = JobName;
    }

    /**
     * @return the JobID
     */
    public String getJobID() {
        return JobID;
    }

    /**
     * @param JobID the JobID to set
     */
    public void setJobID(String JobID) {
        this.JobID = JobID;
    }

    /**
     * @return the JobUserName
     */
    public String getJobUserName() {
        return JobUserName;
    }

    /**
     * @param JobUserName the JobUserName to set
     */
    public void setJobUserName(String JobUserName) {
        this.JobUserName = JobUserName;
    }

    /**
     * @return the JobFailureInfo
     */
    public String getJobFailureInfo() {
        return JobFailureInfo;
    }

    /**
     * @param JobFailureInfo the JobFailureInfo to set
     */
    public void setJobFailureInfo(String JobFailureInfo) {
        this.JobFailureInfo = JobFailureInfo;
    }

    /**
     * @return the JobIsComplete
     */
    public boolean getJobIsComplete() {
        return JobIsComplete;
    }

    /**
     * @param JobIsComplete the JobIsComplete to set
     */
    public void setJobIsComplete(boolean JobIsComplete) {
        this.JobIsComplete = JobIsComplete;
    }

    /**
     * @return the JobMapProgress
     */
    public int getJobMapProgress() {
        return JobMapProgress;
    }

    /**
     * @param JobMapProgress the JobMapProgress to set
     */
    public void setJobMapProgress(int JobMapProgress) {
        this.JobMapProgress = JobMapProgress;
    }

    /**
     * @return the JobReduceProgress
     */
    public int getJobReduceProgress() {
        return JobReduceProgress;
    }

    /**
     * @param JobReduceProgress the JobReduceProgress to set
     */
    public void setJobReduceProgress(int JobReduceProgress) {
        this.JobReduceProgress = JobReduceProgress;
    }


    
}
