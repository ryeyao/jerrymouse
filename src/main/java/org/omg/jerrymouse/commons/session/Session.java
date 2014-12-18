package org.omg.jerrymouse.commons.session;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: rye
 * Date: 12/10/14
 * Time: 15:13
 */
public class Session<K, V> extends ConcurrentHashMap<K, V> {

    private String session_id = null;
    private long max_seconds_to_live = 3600;
    private long seconds_to_live = 0;
    private Date last_visited;
    private Date created;
    private boolean active;

    public Session(String session_id) {
        this.session_id = session_id;
        seconds_to_live = max_seconds_to_live;
        created = new Date();
        this.active();
    }

    public long getMaxSecondsToLive() {
        return max_seconds_to_live;
    }

    public void setMaxSecondsToLive(long max_seconds_to_live) {
        this.max_seconds_to_live = max_seconds_to_live;
    }

    public Date getLastVisited() {
        return last_visited;
    }

    public void active() {
        this.active = true;
        this.last_visited = new Date();
    }

    public void visit() {
        active();
    }

    public void deactive() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public Date getCreated() {
        return created;
    }

    public boolean isExpired() {
        if (new Date().getTime() - last_visited.getTime() >= seconds_to_live * 1000) {
            this.deactive();
            return true;
        }
        return false;
    }

    public long getSecondsToLive() {
        return seconds_to_live;
    }

    public void setSecondsToLive(long seconds_to_live) {
        if (seconds_to_live > max_seconds_to_live) {
            return;
        }
        this.seconds_to_live = seconds_to_live;
    }

    public String getSessionID() {
        return session_id;
    }

    public void setSessionID(String session_id) {
        this.session_id = session_id;
    }
}
