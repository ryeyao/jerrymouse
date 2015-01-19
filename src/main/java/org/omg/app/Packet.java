package org.omg.app;

/**
 * User: rye
 * Date: 12/30/14
 * Time: 16:01
 */
public class Packet<T> {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Packet<?> apply(ITransformer transformer) {
        return  applyInternal(transformer);
    }

    private Packet<?> applyInternal(ITransformer transformer) {
        return transformer.transform(this);
    }
}
