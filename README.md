scape-tavernahadoop-demonstrator
================================

Introduction
------------

The `scape-tavernahadoop-demonstrator` project consists of the modules 
`defaulthttp-restclient`, `tavernaserver-restclient`, `hadoopjobtracker-client` 
and `tavernahadoop-webgui` which together demonstrate the orchestration of 
Hadoop Jobs using the Taverna Server.

The `tavernahadoop-webgui` is the web gui application which can be deployed to 
an Apache Tomcat Web Application Server  (deployment has been tested on versions 
6.26 and 7.0.32).

The `tavernaserver-restclient` is a generic REST client including a client for the 
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

Installation requirements
-------------------------

In order to build the application, Java SE Development Kit (JDK) version >=1.6.0 
and Apache Maven >= 2.2.1 is required. The web application needs a  
Taverna Server 2.4 server instance, Hadoop (CDH3u4) - at least running in 
pseudo-distributed mode - and a MySQL Server version >= 5.2. For web application 
deployment a servlet container, such as the Apache Tomcat Web Application Server 
is required (deployment has been tested on versions 6.26 and 7.0.32).

Regarding the installation of Taverna Server 2.4 follow the instructions here:

http://dev.mygrid.org.uk/wiki/display/taverna/Installation+and+Configuration

And for a minimum hadoop CDH4 installation in pseudo-distributed mode, see here:

http://www.cloudera.com/content/cloudera-content/cloudera-docs/CDH4/4.2.0/CDH4-Quick-Start/cdh4qs_topic_3_3.html

Configuration & Deployment of the web application
-------------------------------------------------

Copy the configuration template files:

    tavernahadoop-webgui/src/main/resources/hadoop_template.properties
    tavernahadoop-webgui/src/main/resources/hibernate_template.cfg.xml
    tavernahadoop-webgui/src/main/resources/taverna_template.properties
    tavernahadoop-webgui/src/main/resources/myexperiment_template.properties

to:

    tavernahadoop-webgui/src/main/resources/hadoop.properties
    tavernahadoop-webgui/src/main/resources/hibernate.cfg.xml
    tavernahadoop-webgui/src/main/resources/taverna.properties
    tavernahadoop-webgui/src/main/resources/myexperiment.properties

Configuration is then done in five steps:

1. Configure the Hadoop settings:

    `tavernahadoop-webgui/src/main/resources/hadoop.properties`

    and make sure the dependencies of the Hadoop client are fulfilled:

    `hadoopjobtracker-client/pom.xml`

    It is necessary to use exactly the libraries from your Hadoop installation. 
    Otherwise there might be problems communicating with the JobTracker.
    The current configuration in the `pom.xml` file is set for a CDH3u4 installation.
    The library versions of `hadoop core`, `guava`, `jackson-core-asl` and 
    `jackson-mapper-asl` are required to be the same as the libraries used by 
    the Hadoop installation.
    Look up the exact versions for the depending libraries (specified in the 
    `hadoopjobtracker-client/pom.xml`) at `/usr/lib/hadoop/` and 
    `/usr/lib/hadoop/lib/` on your cluster controller machine.
    Pick up the required libraries from `/usr/lib/hadoop/` and 
    `/usr/lib/hadoop/lib/` and install them in your local repository using 
    `mvn install:install-file` (please refer to the official maven documentation) 
    and adapt the dependency versions in the pom.xml appropriately if needed.
    Example: `mvn install:install-file -Dfile=your.jar -DgroupId=yourGroup -DartifactId=yourArtifact -Dversion=yourVersion -Dpackaging=jar`

2. Configure the Hibernate settings for accessing the MySQL server:
    
    `tavernahadoop-webgui/src/main/resources/hibernate.cfg.xml`

3. Configure the Taverna Server settings for accessing the REST API of the Taverna 
   Server:

    `tavernahadoop-webgui/src/main/resources/taverna.properties`

3. Configure the myExperiment settings for accessing the REST API of myExperiment:

    `tavernahadoop-webgui/src/main/resources/myexperiment.properties`

4. Copy the `tavernahadoop-webgui/settings.xml` into your local maven repository 
   folder:

    `cp tavernahadoop-webgui/settings.xml $HOME/.m2/`

If you already have a settings.xml, make the tomcat deployment profile

``` xml   
    <profile>
        <id>tomcat-deployment-profile</id>
        <properties>
          <tomcat.server.manager.url>http://localhost:8080/manager</tomcat.server.manager.url>
          <tomcat.server.manager.user>tomcat</tomcat.server.manager.user>
          <tomcat.server.manager.password>tomcat</tomcat.server.manager.password>
        </properties>
    </profile>`
```

available as a child of the "profiles" node and make sure the profile is 
activated:

``` xml
    <activeProfiles>
        <activeProfile>tomcat-deployment-profile</activeProfile>
    </activeProfiles>
```

In order to deploy the web application, change to the web application module
directory and run the tomcat:deploy maven task:

    cd tavernahadoop-webgui
    mvn tomcat:deploy

The application will then be deployed at the context path /tavernahadoop-webgui.
For any subsequent deployment run:

    cd tavernahadoop-webgui
    mvn tomcat:redeploy

which will first undeploy the existing web application before deployment.

Check then if the application is running at

    http://${tomcat.server.host}:${tomcat.server.port}/tavernahadoop-webgui

Default HTTP REST Client
------------------------

This generic REST client (module `defaulthttp-restclient`) is based on Apache's
`org.apache.http.impl.client.DefaultHttpClient`. It can perform
GET, PUT, POST, and DELETE requests with Basic HTTP authentication over HTTP 
or HTTPS.

``` text
                        [DefaultHttpClient]
                             (Apache)
                                |
                                |
                                |
                      [DefaultHttpRestClient]
                        (Basic REST Client)
                         |              |
                        |                |
                       |                  |
     [DefaultHttpAuthRestClient]   [DefaultHttpsRestClient]
   (REST Client w. Authentication)     (SSL REST Client)
                 |
                 |
                 |
    [DefaultHttpsAuthRestClient]
 (SSL REST Client w. Authentication)
```

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

Troubleshooting
---------------

1.  HTTP-Error: HTTP 302 Moved Temporarily/Found

    At the current state, the TavernaServerRestClient is configured to use the 
    client with an "insecure" configuration of the Taverna Server.

    See http://dev.mygrid.org.uk/wiki/display/taverna/A+Beginner%27s+Installation+Guide+to+Taverna+Server
    for more information on how to set up a "secure" or "insecure" instance
    of the Taverna Server.

    The "secure" or "insecure" mode is configured in
    `apache-tomcat-${tomcat-version}/webapps/TavernaServer.2.4.1/WEB-INF/web.xml`
    Comment/uncomment one of the context parameters (default is secure mode) to 
    activate either the secure or the insecure profile:

``` xml
    <param-value>WEB-INF/secure.xml</param-value>
    <!--param-value>WEB-INF/insecure.xml</param-value-->
```

    If the Taverna Server is configured in "secure" mode, the http request url
    `http://${server}:8080/TavernaServer.2.4.1/rest/runs` is relocated to the 
    https request url `https://${server}:8443/TavernaServer.2.4.1/rest/runs` by
    the server. This requires an HTTP client with SSL support. 
    
    The defaulthttp-restclient module supports https requests by using the
    `eu.scape_project.tb.rest.DefaultHttpsAuthRestClient` instead of the 
    `eu.scape_project.tb.rest.DefaultHttpAuthRestClient` class. So this behaviour 
    can be changed by extending `eu.scape_project.tb.rest.DefaultHttpsAuthRestClient` 
    instead of `eu.scape_project.tb.rest.DefaultHttpAuthRestClient` 
    in the `eu.scape_project.tb.taverna.rest.TavernaServerRestClient` class.
