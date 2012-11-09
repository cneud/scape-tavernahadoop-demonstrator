scape-tavernahadoop-demonstrator
================================

Introduction
------------

The `scape-tavernahadoop-demonstrator` project consists of the three modules 
`defaulthttp-restclient`, `tavernaserver-restclient`, and `tavernahadoop-webgui`
which together demonstrate the orchestration of Hadoop Jobs using the Taverna 
Server.

The main application is the `tavernahadoop-webgui` which can be deployed to an
Apache Tomcat Web Application Server (tested on version 6.26).

The tavernaserver-restclient is a generic REST client including a client for the 
Taverna Server 2.4 which is available for download at 
http://www.taverna.org.uk/download/server/2-4/. This client is a simple and 
reduced alternative to the full Taverna Server client available at
https://github.com/myGrid/t2-server-jar. It consists of a generic REST client 
(module `defaulthttp-restclient`) based on 
`org.apache.http.impl.client.DefaultHttpClient` which can do Basic HTTP 
authentication, and a basic Taverna REST client consuming the Taverna Server 
2.4 REST API for submitting, starting, and monitoring Taverna Server Workflow 
Runs. Taverna Server 2.4 REST API is documented at 
http://dev.mygrid.org.uk/wiki/display/taverna/REST+API.

Current status
--------------

At the current state, the prototype of the Taverna Server workflow is in place,
the Hadoop integration is being planned.

Installation requirements
-------------------------

For building the application, the Java SE Development Kit (JDK) version >=1.6.0 
and Apache Maven >= 2.2.1 is required. The web application needs a running 
Taverna Server 2.4 instance, and Hadoop at least running in pseudo-distributed 
mode and a MySQL Server version >= 5.2. For deployment of the web application 
a servlet container, e.g. the Apache Tomcat Web Application Server is needed 
(tested on 6.26 and 7.0.32).

For installing the Taverna Server 2.4 follow the instructions here:

http://dev.mygrid.org.uk/wiki/display/taverna/Installation+and+Configuration

And for a minimum hadoop installation in pseudo-distributed mode, see here:

https://ccp.cloudera.com/display/CDHDOC/Installing+CDH3+on+a+Single+Linux+Node+in+Pseudo-distributed+Mode

Configuration & Deployment of the web application
-------------------------------------------------

Rename the configuration template files:

    tavernahadoop-webgui/src/main/resources/taverna_template.properties
    tavernahadoop-webgui/src/main/resources/hibernate_template.cfg.xml

to:

    tavernahadoop-webgui/src/main/resources/taverna.properties
    tavernahadoop-webgui/src/main/resources/hibernate.cfg.xml

Configuration is then done in four steps:

1. Adapt the Taverna Server settings:

    `tavernahadoop-webgui/src/main/resources/taverna.properties`

2. Adapt the Hibernate settings according to your MySQL server:
    
    `tavernahadoop-webgui/src/main/resources/hibernate.cfg.xml`

3. Copy the `tavernahadoop-webgui/settings.xml` into your local maven repository 
   folder:

    `cp tavernahadoop-webgui/settings.xml $HOME/.m2/`

   If you already have a settings.xml, make the tomcat deployment profile
   
    `<profile>
        <id>tomcat-deployment-profile</id>
        <properties>
          <tomcat.server.manager.url>http://localhost:8080/manager</tomcat.server.manager.url>
          <tomcat.server.manager.user>tomcat</tomcat.server.manager.user>
          <tomcat.server.manager.password>tomcat</tomcat.server.manager.password>
        </properties>
    </profile>`

    available as a child of the "profiles" node and make sure the profile is activated:

    `<activeProfiles>
        <activeProfile>tomcat-deployment-profile</activeProfile>
    </activeProfiles>`

4. Adapt the settings for the Hadoop Job Tracker API:

   `STILL TO BE DEFINED`

In order to deploy the web application, change to the web application module
directory and run the corresponding maven task:

    cd tavernahadoop-webgui
    mvn tomcat:deploy

The application will then be deployed at the context path /tavernahadoop-webgui.
For any subsequent deployment run:

    cd tavernahadoop-webgui
    mvn tomcat:redeploy

which will first undeploy the existing web application before deployment.

Check then if the application is running at

    http://${tomcat.server.host}:${tomcat.server.port}/tavernahadoop-webgui

Taverna Server 2.4 REST client
------------------------------

Properties of the `TavernaServerRestClient` bean are defined in the properties 
file tavernahttp-restclient/src/main/resources/properties/tavernaconfig.properties. 
Host, port, base URL fields are provided at construction time, the object, 
username and password are defined afterwards.

A demonstration of the client is provided in the `tavernaserver-restclient`
module by executing the main class `TavernaServerRestClientDemo`.

1.  *Submit Workflow* (`TavernaServerRestClient.submitWorkflow`). 
    The client starts by creating a workflow run. A workflow file is submitted to 
    the Taverna Server using `TavernaServerRestClient.submitWorkflow`. The method 
    returns the UUID base resource URL which is the handler for subsequent requests 
    related to this resource. The workflow is in "initialised" status, waiting for 
    input ports to be populated or to be started. 
2.  *Set input port value(s)* (`TavernaServerRestClient.setWorkflowInput`) 
    In the next step, the input port(s) of the workflow can be set. This is done 
    using `TavernaServerRestClient.setWorkflowInput` by indicating the UUID base 
    resource URL, the port name and the corresponding value. 
3.  *Start workflow run* (`TavernaServerRestClient.setWorkflowStatus`). 
    Now the workflow run can be started. This is done by setting the workflow status 
    to `TavernaWorkflowStatus.OPERATING` using 
    `TavernaServerRestClient.setWorkflowStatus`. 
4.  *Polling workflow run status* (`TavernaServerRestClient.getWorkflowStatus`). 
    While the workflow is running, the status can be polled using 
    `TavernaServerRestClient.getWorkflowStatus`. If the status is 
    `TavernaWorkflowStatus.OPERATING`, the workflow is still running. If the 
    workflow status is `TavernaWorkflowStatus.FINISHED`, the workflow run is finished.
5.  *Get current workflow runs* (`TavernaServerRestClient.getCurrentTavernaRuns`).
    List all current workflow runs. This method will return a string list containing
    the UUIDs off the registered workflow runs.
6.  *Delete (a) workflow run(s)* (`TavernaServerRestClient.deleteWorkflow/deleteAllRuns`).
    Workflow run(s) can be deleted using `TavernaServerRestClient.deleteWorkflow` by
    indicating the UUID base resource URL or using `TavernaServerRestClient.deleteAllRuns`
    which lists current workflow runs and deletes them all.
