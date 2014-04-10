package cn.iie.gaia.util;

import cn.iie.gaia.LifecycleException;
import cn.iie.gaia.LifecycleState;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/4/14
 * Time: 10:31 AM
 */
public abstract class LifecycleMBeanBase extends LifecycleBase {
    @Override
    protected void initInternal() throws LifecycleException {
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }
    /**
     * Sub-classes wishing to perform additional clean-up should override this
     * method, ensuring that super.destroyInternal() is the last call in the
     * overriding method.
     */
    @Override
    protected void destroyInternal() throws LifecycleException {
    }

    @Override
    protected void updateInternal() throws LifecycleException {

    }

}
