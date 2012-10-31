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
package eu.scape_project.tb.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate Util. Helper class that takes care of startup for accessing the
 * org.hibernate.SessionFactory. Annotated hibernate classes are registered
 * here.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration c = new Configuration();
            c.addAnnotatedClass(eu.scape_project.tb.model.entity.Workflow.class);
            c.addAnnotatedClass(eu.scape_project.tb.model.entity.WorkflowRun.class);
            c.addAnnotatedClass(eu.scape_project.tb.model.entity.WorkflowInputPort.class);
            // Create the SessionFactory from hibernate.cfg.xml

            return c.configure().buildSessionFactory();

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}