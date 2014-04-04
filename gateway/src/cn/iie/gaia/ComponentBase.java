package cn.iie.gaia;

import cn.iie.gaia.util.LifecycleBase;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/1/14
 * Time: 4:38 PM
 */
public abstract class ComponentBase extends LifecycleBase implements Component {

    private String name = null;
    private ClassLoader classLoader = null;
    private Container container = null;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }
}
