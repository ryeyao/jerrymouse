package org.jerrymouse.net.mq;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: rye
 * Date: 12/4/14
 * Time: 16:44
 */
public class MessageQueue<T> extends LinkedBlockingQueue<T> {

    private String queue_id;

    public MessageQueue(int capacity) {
        super(capacity);
    }

    public MessageQueue() {
        super();
    }

    public String getQueueID() {
        return queue_id;
    }

    public void setQueueID(String queue_id) {
        this.queue_id = queue_id;
    }


}
