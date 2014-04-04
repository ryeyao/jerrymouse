package cn.iie.gaia;

import cn.iie.gaia.util.LifecycleBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/4/14
 * Time: 2:23 PM
 */
public abstract class ContainerBase extends LifecycleBase implements Container {

    private String name = null;
    protected Container parent = null;
    private ClassLoader parentClassLoader = null;

    private Map<String, Container> childs = new HashMap<String, Container>();

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
    public String getPath() {
        if(parent == null) {
            return "/" + name;
        }
        return parent.getPath() + "/" + name;
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
