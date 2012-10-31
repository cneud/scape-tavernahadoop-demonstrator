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

import java.io.IOException;
import javax.servlet.*;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate Session Request Filter. See
 * https://community.jboss.org/wiki/OpenSessionInView. Servlet filter for
 * creating and closing the hibernate session. This is an interceptor that runs
 * after the view has been rendered, and that will then commit the database
 * transaction, hence close the Session. When an HTTP request has to be handled,
 * a new Session and database transaction will begin. Right before the response
 * is send to the client, and after all the work has been done, the transaction
 * will be committed, and the Session will be closed.
 *
 * @author Steve Ebersole https://community.jboss.org/people/sebersole
 * @version 0.1
 */
public class HibernateSessionRequestFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(HibernateSessionRequestFilter.class.getName());
    private SessionFactory sf;

    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        try {
            logger.debug("Starting a database transaction");
            sf.getCurrentSession().beginTransaction();

            // Call the next filter (continue request processing)
            chain.doFilter(request, response);

            // Commit and cleanup
            logger.debug("Committing the database transaction");
            sf.getCurrentSession().getTransaction().commit();

        } catch (StaleObjectStateException staleEx) {
            logger.error("This interceptor does not implement optimistic concurrency control!");
            logger.error("Your application will not work until you add compensation actions!");
            // Rollback, close everything, possibly compensate for any permanent changes
            // during the conversation, and finally restart business conversation. Maybe
            // give the user of the application a chance to merge some of his work with
            // fresh data... what you do here depends on your applications design.
            throw staleEx;
        } catch (Throwable ex) {
            // Rollback only
            logger.error("Hibernate error", ex);
            try {
                if (sf.getCurrentSession().getTransaction().isActive()) {
                    logger.debug("Trying to rollback database transaction after exception");
                    sf.getCurrentSession().getTransaction().rollback();
                }
            } catch (Throwable rbEx) {
                logger.error("Could not rollback transaction after exception!", rbEx);
            }

            // Let others handle it... maybe another interceptor for exceptions?
            throw new ServletException(ex);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Initializing filter...");
        logger.debug("Obtaining SessionFactory from static HibernateUtil singleton");
        sf = HibernateUtil.getSessionFactory();
    }

    @Override
    public void destroy() {
    }
}
