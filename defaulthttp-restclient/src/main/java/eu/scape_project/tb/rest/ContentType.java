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
package eu.scape_project.tb.rest;

/**
 * Content types. This class is a placeholder of the apache class
 * org.apache.http.entity.ContentType which is available in newer versions of
 * the Apache HttpCore API.
 *
 * @author shsdev https://github.com/shsdev
 * @version 1.0
 */
public enum ContentType {

    /**
     * Taverna workflow xml mime type
     */
    APPLICATION_TAVERNA_XML {

        @Override
        public String toString() {
            return "application/vnd.taverna.t2flow+xml";
        }
    },
    APPLICATION_ATOM_XML {

        @Override
        public String toString() {
            return "application/atom+xml";
        }
    },
    APPLICATION_FORM_URLENCODED {

        @Override
        public String toString() {
            return "application/x-www-form-urlencoded";
        }
    },
    APPLICATION_JSON {

        @Override
        public String toString() {
            return "application/json";
        }
    },
    APPLICATION_OCTET_STREAM {

        @Override
        public String toString() {
            return "application/octet-stream";
        }
    },
    APPLICATION_SVG_XML {

        @Override
        public String toString() {
            return "application/svg+xml";
        }
    },
    APPLICATION_XHTML_XML {

        @Override
        public String toString() {
            return "application/xhtml+xml";
        }
    },
    APPLICATION_XML {

        @Override
        public String toString() {
            return "application/xml";
        }
    },
    MULTIPART_FORM_DATA {

        @Override
        public String toString() {
            return "multipart/form-data";
        }
    },
    TEXT_HTML {

        @Override
        public String toString() {
            return "text/html";
        }
    },
    TEXT_PLAIN {

        @Override
        public String toString() {
            return "text/plain";
        }
    },
    TEXT_XML {

        @Override
        public String toString() {
            return "text/xml";
        }
    }
}