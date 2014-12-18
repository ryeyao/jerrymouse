package org.jerrymouse.net;

import org.jerrymouse.net.exception.MalformedMessageException;

/**
 * User: rye
 * Date: 12/11/14
 * Time: 9:48
 */
public interface IDataParser {

    public IDataItem parse(byte[] data, int len) throws MalformedMessageException;

    public byte[] pack(IDataItem data);
}
