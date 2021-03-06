package org.omg.gaia.entity;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.omg.gaia.ConfigProperties;
import org.omg.gaia.LifecycleException;
import org.omg.gaia.util.LifecycleMBeanBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/5/14
 * Time: 1:14 PM
 */
public class ConfigPropertiesBase extends LifecycleMBeanBase implements ConfigProperties {

    private static final Logger log = LoggerFactory.getLogger(ConfigPropertiesBase.class);
    private Properties properties = null;
    private String componentHome = null;


    @Override
    public String getComponentHome() {
        return componentHome;
    }

    @Override
    public void setComponentHome(String name) {
        this.componentHome = name;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    protected void initInternal() throws LifecycleException {

    }

    @Override
    protected void updateInternal() throws LifecycleException {
        loadProperties();
    }

    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        loadProperties();
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        properties = null;
    }

    private void loadProperties() {
        File propFile = new File(componentHome, "component.properties");

        properties = new Properties();
        try {
            properties.load(new FileInputStream(propFile));
        } catch (IOException e) {
            log.error("component.properties not found");
        }
    }
}
