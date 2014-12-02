package main.java.cn.iie.gaia.util;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/8/14
 * Time: 5:47 PM
 */
public abstract class StoppableLoopThread extends Thread {

    /**
     * Do not write eternal loop code inside this method!
     */
    public abstract void loopTask();

    @Override
    public void run() {
        while(!isInterrupted()) {
            loopTask();
        }
    }

    public void shutdown() {
        this.interrupt();
    }
}
