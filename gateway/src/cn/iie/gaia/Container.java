package cn.iie.gaia;

import cn.iie.gaia.util.LifecycleMBeanBase;

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

    public String getPath();
}
