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

import java.io.File;

/**
 * Utility class for creating a REST resource path.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 1.0
 */
public class FileUtility {

    /**
     * Create rest resource path.
     *
     * @param strings Parts of the rest resource path.
     * @return rest resource path
     */
    public static String makePath(String... strings) {
        String result = "";
        for (int i = 0; i < strings.length; i++) {
            String item = strings[i];
            // Remove leading path separator, if any
            if (item.charAt(0) == File.separatorChar) {
                item = item.substring(1, item.length());
            }
            // Remove trailing path separator, if any
            if (item.charAt(item.length() - 1) == File.separatorChar) {
                item = item.substring(0, item.length() - 1);
            }
            if (!item.startsWith("http")) {
                result += File.separator;
            }
            result += item;
        }
        return result;
    }
}
