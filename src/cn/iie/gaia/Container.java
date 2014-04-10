package cn.iie.gaia;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/4/14
 * Time: 2:21 PM
 */
public interface Container extends Lifecycle {

    public void setName(String name);
    public String getName();

    public void setParent(Container parent);

    public void setParentClassLoader(ClassLoader classLoader);
    public ClassLoader getParentClassLoader();

    public void addChild(Container child);
    public void removeChild(Container child);
    public Collection<Container> childs();

    public void addComponent(Component component);
    public void removeComponent(Component component);
    public Collection<Component> components();

    public String getPath();
}
