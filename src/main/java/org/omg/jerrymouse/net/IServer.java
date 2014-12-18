package org.omg.jerrymouse.net;

/**
 * User: rye
 * Date: 12/4/14
 * Time: 16:03
 */
public interface IServer extends Runnable {

    public boolean addInterceptor(IDataHandler dh);

    public boolean setDataHandler(IDataHandler dh);

}
