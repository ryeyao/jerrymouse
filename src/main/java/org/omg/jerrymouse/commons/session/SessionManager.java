package org.omg.jerrymouse.commons.session;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: rye
 * Date: 12/10/14
 * Time: 15:04
 */
public class SessionManager extends ConcurrentHashMap<String, Session> {

    private SessionManager() {
    }

    public static SessionManager instance() {
        return SessionManagerSingleton.instance;
    }

    private static final class SessionManagerSingleton {
        private static final SessionManager instance = new SessionManager();
    }
}
