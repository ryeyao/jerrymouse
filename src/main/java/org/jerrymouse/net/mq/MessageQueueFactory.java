package org.jerrymouse.net.mq;

/**
 * User: rye
 * Date: 12/10/14
 * Time: 16:54
 */
public class MessageQueueFactory {

    public static MessageQueueFactory getFactory() {
        return MessageQueueFactorySingleton.instance;
    }

    public MessageQueue newMessageQueue() {
        return new MessageQueue();
    }

    public MessageQueue newMessageQueue(int max_size) {
        return new MessageQueue(max_size);
    }

    private static class MessageQueueFactorySingleton {
        private static final MessageQueueFactory instance = new MessageQueueFactory();
    }
}
