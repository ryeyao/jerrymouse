package cn.iie.gaia.entity;

import cn.iie.gaia.ConfigProperties;
import cn.iie.gaia.Container;
import cn.iie.gaia.LifecycleException;
import cn.iie.gaia.util.LifecycleBase;
import cn.iie.gaia.util.LifecycleMBeanBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger log = LogManager.getLogger(ConfigPropertiesBase.class);
    private Properties properties = null;
    private Container container = null;


    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
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
        String propDir = container.getPath();
        File propFile = new File(propDir, "component.properties");

        properties = new Properties();
        try {
            properties.load(new FileInputStream(propFile));
        } catch (IOException e) {
            log.error("component.properties not found");
        }
    }
}
