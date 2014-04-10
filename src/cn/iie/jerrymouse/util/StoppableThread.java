package cn.iie.jerrymouse.util;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/8/14
 * Time: 5:47 PM
 */
public abstract class StoppableThread extends Thread {

    /**
     * Do not write eternal loop code inside this method!
     */
    public abstract void runTask();

    @Override
    public void run() {
        while(!isInterrupted()) {
            runTask();
        }
    }
}
