package cn.iie.gaia.entity;

import cn.iie.gaia.Component;
import cn.iie.gaia.Container;
import cn.iie.gaia.util.LifecycleMBeanBase;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/4/14
 * Time: 2:23 PM
 */
public abstract class ContainerBase extends LifecycleMBeanBase implements Container {

    private String name = null;
    protected Container parent = null;
    private ClassLoader parentClassLoader = null;

    private Map<String, Container> childs = new HashMap<String, Container>();
    private Map<String, Component> components = new HashMap<String, Component>();


    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(Container child) {
        childs.put(child.getName(), child);
    }

    @Override
    public void removeChild(Container child) {
        childs.remove(child.getName());
    }

    @Override
    public Collection<Container> childs() {
        return childs.values();
    }

    @Override
    public void addComponent(Component component) {
        components.put(component.getName(), component);
    }

    @Override
    public void removeComponent(Component component) {
        components.remove(component.getName());
    }

    @Override
    public Collection<Component> components() {
        return components.values();
    }

    @Override
    public String getPath() {
        if(parent == null) {
            return name;
        }
        return parent.getPath() + File.separatorChar + name;
    }

    @Override
    public void setParentClassLoader(ClassLoader classLoader) {
        this.parentClassLoader = classLoader;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        return this.parentClassLoader;
    }

}
