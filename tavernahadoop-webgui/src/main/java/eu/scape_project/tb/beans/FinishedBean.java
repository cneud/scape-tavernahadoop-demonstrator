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
package eu.scape_project.tb.beans;

import eu.scape_project.tb.model.entity.WorkflowRun;
import eu.scape_project.tb.taverna.WebAppTavernaRestClient;
import eu.scape_project.tb.taverna.rest.KeyValuePair;
import eu.scape_project.tb.taverna.rest.TavernaClientException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing bean of the 'finished' JSF page. Accessing the output a workflow run
 * that has terminated.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@ManagedBean(name = "finished")
@SessionScoped
public class FinishedBean implements Serializable {

    private List<KeyValuePair> outputValues;
    private static Logger logger = LoggerFactory.getLogger(FinishedBean.class.getName());
    private WorkflowRun selectedWorkflowRun;

    @PostConstruct
    public void initFinishedBean() {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            ProgressBean progress = (ProgressBean) externalContext.getSessionMap().get("progress");
            this.selectedWorkflowRun = progress.getSelectedWorkflowRun();

            WebAppTavernaRestClient tavernaRestClient = WebAppTavernaRestClient.getInstance();

            String urlStr = this.selectedWorkflowRun.getUuidBaseResourceUrl();
            URL url = new URL(urlStr);
            outputValues = tavernaRestClient.getClient().getWorkflowRunOutputValues(url);

        } catch (TavernaClientException ex) {
            logger.error("HTTP error", ex);
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL error", ex);
        }
    }

    public List getOutputValues() {
        return outputValues;
    }

    public void setOutputValues(List outputValues) {
        this.outputValues = outputValues;
    }
}
