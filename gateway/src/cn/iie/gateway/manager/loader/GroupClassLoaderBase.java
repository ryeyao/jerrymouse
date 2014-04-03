package cn.iie.gateway.manager.loader;

import cn.iie.gateway.manager.lifecycle.LifecycleException;
import cn.iie.gateway.manager.lifecycle.util.LifecycleBase;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/2/14
 * Time: 3:07 PM
 */
public class GroupClassLoaderBase extends LifecycleBase implements GroupClassLoader{

    private String classpath = ".";

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

    @Override
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    @Override
    public String getClasspath() {
        return this.classpath;
    }
}
