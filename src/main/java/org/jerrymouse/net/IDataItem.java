package org.jerrymouse.net;

import java.net.SocketAddress;

/**
 * User: rye
 * Date: 12/4/14
 * Time: 16:14
 */
public interface IDataItem {

    public byte[] getData();

    public void setData(byte[] data);

    public int getDataLength();

    public void setDataLength(int length);

    public SocketAddress getSource();

    public void setSource(SocketAddress source);

    public SocketAddress getTarget();

    public void setTarget(SocketAddress target);
}
