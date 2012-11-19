hadoopjobtracker-client
=======================

The `hadoopjobtracker-client` is used within the `scape-tavernahadoop-demonstrator` project to retrieve clusterstatus- and Hadoop job-information from the Hadoop cluster using the Hadoop JobClient interface.

How it works
------------

A `UUID` marker is used to identify all jobs belonging to a particular workflow run. This is necessary because one workflow might launch multiple Hadoop jobs, or a single PIG based job might split into  multiple Hadoop jobs during run time.
To enable `hadoopjobtracker-client` finding all jobs with a specific `UUID`, you need to pass the `UUID` within the `job name`. This can be done on the command line for native (java) map/reduce programs and PIG jobs.
Make sure you do not set the `job name` in the code of map / reduce programs. This will override the `job name` set on the command line and make it therefore impossible to set it from outside.

Available functions
-------------------

1. Get the status of all jobs on the cluster (since JobTracker (re-)start).
2. Get the status of all jobs containing a specific `UUID` (set by the workflow on job start) marker in the `job name`.
3. Get some status information from the cluster (Number of active Nodes, max MAP tasks, ... ).


Usage
-----

Have a look at the `HadoobJobTrackerClientDemo` class to see usage examples.

Configuration
-------------

Set the hostname and port number of your cluster JobTracker according to the example class `HadoobJobTrackerClientDemo`.

Dependencies
------------

Make sure you are using exactly the same libraries as your Hadoop installation is using. Otherwise you might notice problems connecting or retrieving data from your JobTracker.
The current configuration is set for a CDH3u4 installation.


