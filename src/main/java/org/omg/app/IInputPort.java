package org.omg.app;

/**
 * User: rye
 * Date: 12/30/14
 * Time: 15:30
 */
public interface IInputPort<T> extends IPort<T> {

    public void write(T t);

}
