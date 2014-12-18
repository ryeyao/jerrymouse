package org.jerrymouse.commons.util;

/**
 * User: rye
 * Date: 12/11/14
 * Time: 10:35
 */
public class MACUtils {

    public static String getMACStringFromBytes(byte[] bytes) {
        return StringUtils.getStringFromBytes(bytes, "%02x", ":");
    }

    public static byte[] getBytesFromMACString(String mac, int len) {
        return StringUtils.getBytesFromString(mac, ":", len, 16);
    }
}
