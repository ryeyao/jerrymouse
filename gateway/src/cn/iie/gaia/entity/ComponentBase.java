package cn.iie.gaia.entity;

import cn.iie.gaia.Component;
import cn.iie.gaia.ConfigProperties;
import cn.iie.gaia.Container;
import cn.iie.gaia.LifecycleException;
import cn.iie.gaia.loader.ComponentClassLoader;
import cn.iie.gaia.util.LifecycleBase;
import cn.iie.gaia.util.LifecycleMBeanBase;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/1/14
 * Time: 4:38 PM
 */
public abstract class ComponentBase extends LifecycleMBeanBase implements Component {

    private String name = null;
    private Container container = null;
    private ConfigProperties properties = null;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Container getContainer() {
        return container;
    }
}
