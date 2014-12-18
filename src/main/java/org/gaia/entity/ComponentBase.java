package org.gaia.entity;

import org.gaia.Component;
import org.gaia.ConfigProperties;
import org.gaia.Container;
import org.gaia.LifecycleException;
import org.gaia.util.LifecycleMBeanBase;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/1/14
 * Time: 4:38 PM
 */
public abstract class ComponentBase extends LifecycleMBeanBase implements Component {

    private String name = null;
    private Container container = null;
    private ConfigProperties configProperties = null;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public String getComponentBase() {
        return container.getPath() + File.separatorChar + name;
    }

    @Override
    public void setConfigProperties(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
    }

    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
    }
}
