package org.jerrymouse.commons.util;

/**
 * User: rye
 * Date: 12/11/14
 * Time: 10:35
 */
public class IPUtils {
    public static String getIPStringFromBytes(byte[] bytes) {
        return StringUtils.getStringFromBytes(bytes, "%d", ".");
    }

    public static byte[] getBytesFromIPString(String ip, int len) {
        return StringUtils.getBytesFromString(ip, ".", len, 10);
    }
}
