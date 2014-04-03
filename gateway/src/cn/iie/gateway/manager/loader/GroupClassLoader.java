package cn.iie.gateway.manager.loader;

import cn.iie.gateway.manager.lifecycle.Lifecycle;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/2/14
 * Time: 3:03 PM
 */
public interface GroupClassLoader extends Lifecycle {

    public void setClasspath(String classpath);
    public String getClasspath();
}
