package org.iie;

import org.gaia.LifecycleException;
import org.gaia.entity.StandardComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/5/14
 * Time: 6:18 PM
 */
public class testComp extends StandardComponent {

    @Override
    protected void initInternal() throws LifecycleException {
        System.out.println("Init");
    }

    @Override
    protected void updateInternal() throws LifecycleException {
        System.out.println("Update");

    }

    @Override
    protected void startInternal() throws LifecycleException {
        System.out.println("testComp Start");
        super.startInternal();

    }

    @Override
    protected void stopInternal() throws LifecycleException {

        System.out.println("testComp Stop");
        super.stopInternal();
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        System.out.println("Destory");
    }
}
