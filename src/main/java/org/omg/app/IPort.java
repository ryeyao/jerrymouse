package org.omg.app;

import java.io.Closeable;

/**
 * User: rye
 * Date: 12/30/14
 * Time: 15:32
 */
public interface IPort<T> extends Closeable {

    public void setId(String id);
    public String getId();
}
