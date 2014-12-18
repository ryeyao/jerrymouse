package org.jerrymouse.net;

import java.net.SocketAddress;

/**
 * User: rye
 * Date: 12/17/14
 * Time: 10:12
 */
public class BaseDataItem implements IDataItem {

    private byte[] data = null;
    private int data_len = 0;

    private SocketAddress source = null;
    private SocketAddress target = null;

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int getDataLength() {
        return this.data_len;
    }

    @Override
    public void setDataLength(int length) {
        this.data_len = length;
    }

    @Override
    public SocketAddress getSource() {
        return this.source;
    }

    @Override
    public void setSource(SocketAddress source) {
        this.source = source;
    }

    @Override
    public SocketAddress getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(SocketAddress target) {
        this.target = target;
    }
}
