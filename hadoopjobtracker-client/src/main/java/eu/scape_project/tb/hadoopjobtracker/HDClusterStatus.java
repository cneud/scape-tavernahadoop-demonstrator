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
public class HDClusterStatus {
    
    private static Logger logger = LoggerFactory.getLogger(HDClusterStatus.class.getName());
    
    private String JobTrackerState;
    private int TaskTrackersAvailable;
    private int TaskTrackersBlacklisted;
    private int MaxMapTaskSlots;
    private int MaxReduceTaskSlots;

    /**
     * @return the JobTrackerState
     */
    public String getJobTrackerState() {
        return JobTrackerState;
    }

    /**
     * @param JobTrackerState the JobTrackerState to set
     */
    public void setJobTrackerState(String JobTrackerState) {
        this.JobTrackerState = JobTrackerState;
    }

    /**
     * @return the TaskTrackersAvailable
     */
    public int getTaskTrackersAvailable() {
        return TaskTrackersAvailable;
    }

    /**
     * @param TaskTrackersAvailable the TaskTrackersAvailable to set
     */
    public void setTaskTrackersAvailable(int TaskTrackersAvailable) {
        this.TaskTrackersAvailable = TaskTrackersAvailable;
    }

    /**
     * @return the TaskTrackersBlacklisted
     */
    public int getTaskTrackersBlacklisted() {
        return TaskTrackersBlacklisted;
    }

    /**
     * @param TaskTrackersBlacklisted the TaskTrackersBlacklisted to set
     */
    public void setTaskTrackersBlacklisted(int TaskTrackersBlacklisted) {
        this.TaskTrackersBlacklisted = TaskTrackersBlacklisted;
    }

    /**
     * @return the MapTaskSlotsAvailable
     */
    public int getMaxMapTaskSlots() {
        return MaxMapTaskSlots;
    }

    /**
     * @param MapTaskSlotsAvailable the MapTaskSlotsAvailable to set
     */
    public void setMaxMapTaskSlots(int MapTaskSlotsAvailable) {
        this.MaxMapTaskSlots = MapTaskSlotsAvailable;
    }

    /**
     * @return the ReduceTaskSlotsAvailable
     */
    public int getMaxReduceTaskSlots() {
        return MaxReduceTaskSlots;
    }

    /**
     * @param ReduceTaskSlotsAvailable the ReduceTaskSlotsAvailable to set
     */
    public void setMaxReduceTaskSlots(int ReduceTaskSlotsAvailable) {
        this.MaxReduceTaskSlots = ReduceTaskSlotsAvailable;
    }
    
    
           
}
