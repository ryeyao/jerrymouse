package cn.iie.gateway.manager.loader;

import cn.iie.gateway.manager.lifecycle.LifecycleException;
import cn.iie.gateway.manager.lifecycle.util.LifecycleBase;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/2/14
 * Time: 3:37 PM
 */
public class GroupLoaderBase extends LifecycleBase implements GroupLoader{
    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void addRepository(String repository) {

    }

    @Override
    public String[] findRepositories() {
        return new String[0];
    }

    @Override
    protected void initInternal() throws LifecycleException {

    }

    @Override
    protected void updateInternal() throws LifecycleException {

    }

    @Override
    protected void startInternal() throws LifecycleException {

    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }
}
