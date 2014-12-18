package org.omg.jerrymouse.net.mq;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: rye
 * Date: 12/10/14
 * Time: 16:51
 */
public class MessageQueueManager extends ConcurrentHashMap<String, MessageQueue> {

    private MessageQueueManager() {

    }

    public static MessageQueueManager instance() {
        return MessageQueueManagerSingleton.instance;
    }

    private static final class MessageQueueManagerSingleton {
        private static final MessageQueueManager instance = new MessageQueueManager();
    }
}
