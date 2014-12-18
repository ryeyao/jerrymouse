package org.omg.jerrymouse.net.mq;

import org.omg.jerrymouse.net.IDataHandler;
import org.omg.jerrymouse.net.IDataItem;
import org.omg.jerrymouse.net.IServer;

import java.util.LinkedList;

/**
 * User: rye
 * Date: 12/5/14
 * Time: 10:09
 */
public class MessageQueueProcessor implements IServer {

    private LinkedList<IDataHandler> handlers = new LinkedList<IDataHandler>();
    private boolean started = false;
    private boolean should_stop = false;
    private MessageQueue mq = null;

    public MessageQueueProcessor(MessageQueue mq) {
        this.mq = mq;
    }

    @Override
    public boolean addInterceptor(IDataHandler dh) {

        if (started) {
            return false;
        }

        handlers.addLast(dh);
        return true;
    }

    @Override
    public boolean setDataHandler(IDataHandler dh) {
        return false;
    }

    @Override
    public void run() {
        while (!should_stop) {
            started = true;

            IDataItem msg;
            try {
                msg = (IDataItem) mq.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }


            for (IDataHandler dh : handlers) {
                msg = dh.process(msg);
            }
        }

    }

    public void shutdown() {
        should_stop = true;
    }
}
