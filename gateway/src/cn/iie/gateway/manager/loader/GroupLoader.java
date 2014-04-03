package cn.iie.gateway.manager.loader;

import cn.iie.gateway.manager.lifecycle.Lifecycle;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/2/14
 * Time: 3:33 PM
 */
public interface GroupLoader extends Lifecycle {

    public ClassLoader getClassLoader();
    public void addRepository(String repository);
    public String[] findRepositories();
}
