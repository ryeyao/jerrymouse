/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.omg.gaia.startup;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.omg.gaia.Globals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Utility class to read the bootstrap Gaia configuration.
 *
 * @author Remy Maucherat
 */

public class GaiaProperties {


    // ------------------------------------------------------- Static Variables

    private static final Logger log=
        LoggerFactory.getLogger(GaiaProperties.class);

    private static Properties properties = null;


    static {

        loadProperties();

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return specified property value.
     */
    public static String getProperty(String name) {
    
        return properties.getProperty(name);

    }


    /**
     * Return specified property value.
     *
     * @deprecated  Unused - will be removed in 8.0.x
     */
    @Deprecated
    public static String getProperty(String name, String defaultValue) {

        return properties.getProperty(name, defaultValue);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Load properties.
     */
    private static void loadProperties() {

        InputStream is = null;
        Throwable error = null;

        try {
            String configUrl = getConfigUrl();
            if (configUrl != null) {
                is = (new URL(configUrl)).openStream();
            }
        } catch (Throwable t) {
            handleThrowable(t);
        }

        if (is == null) {
            try {
                System.out.println("GaiaBase: " + getGaiaBase());
                System.out.println("GaiaHome: " + getGaiaHome());
                File home = new File(getGaiaBase());
                File conf = new File(home, "conf");
                File propsFile = new File(conf, "gaia.properties");
                is = new FileInputStream(propsFile);
            } catch (Throwable t) {
                handleThrowable(t);
            }
        }

        if (is == null) {
            try {
                is = GaiaProperties.class.getResourceAsStream
                    ("org/omg/gaia/startup/gaia.properties");
            } catch (Throwable t) {
                handleThrowable(t);
            }
        }

        if (is != null) {
            try {
                properties = new Properties();
                properties.load(is);
                is.close();
            } catch (Throwable t) {
                handleThrowable(t);
                error = t;
            }
        }

        if ((is == null) || (error != null)) {
            // Do something
            log.warn("Failed to load gaia.properties", error);
            // That's fine - we have reasonable defaults.
            properties=new Properties();
        }

        // Register the properties as system properties
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = properties.getProperty(name);
            if (value != null) {
                System.setProperty(name, value);
            }
        }

    }


    /**
     * Get the value of the gaia.home environment variable.
     */
    private static String getGaiaHome() {
        return System.getProperty(Globals.GAIA_HOME_PROP,
                                  System.getProperty("user.dir"));
    }
    
    
    /**
     * Get the value of the gaia.base environment variable.
     */
    private static String getGaiaBase() {

        return System.getProperty(Globals.GAIA_BASE_PROP, getGaiaHome());
    }


    /**
     * Get the value of the configuration URL.
     */
    private static String getConfigUrl() {
        return System.getProperty("gaia.conf");
    }

    // Copied from ExceptionUtils since that class is not visible during start
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently swallowed
    }

}
