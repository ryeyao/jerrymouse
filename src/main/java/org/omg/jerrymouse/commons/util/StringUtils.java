package org.omg.jerrymouse.commons.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

/**
 * User: rye
 * Date: 12/11/14
 * Time: 10:36
 */
public class StringUtils {
    public static String bytesToHexString(byte[] bytes, int len) {

        StringBuilder bb = new StringBuilder();
        int i = 1;
        for (byte b : bytes) {
            bb.append(String.format("%02x", b));
            bb.append(" ");
            if (i % 20 == 0) {
                bb.append("\n\t ");
            }
            i++;
            if (i > len) {
                break;
            }
        }

        return bb.toString();
    }

    public static String getStringByRangeFromBytes(byte[] bytes, int from, int to, String charset) throws UnsupportedEncodingException {

        if (from < 0 || to <= 0) {
            return "";
        }

        if (from >= to) {
            return "";
        }

        if (to - from > bytes.length - 1) {
            return "";
        }

        byte[] sub_array = Arrays.copyOfRange(bytes, from, to);
        return new String(sub_array, charset);
    }

    public static String getStringFromBytes(byte[] bytes, String format_byte, String join_char) {


        StringBuffer sb = new StringBuffer();

        for (byte b : bytes) {
            sb.append(String.format(format_byte, 0x00ff & (short) b));
            sb.append(join_char);
        }

        return sb.substring(0, sb.length() - 1);
    }

    public static byte[] getBytesFromString(String str, String delimiter, int max_bytes, int radix) {

        try (Scanner s = new Scanner(str);) {
            ByteBuffer bb = ByteBuffer.allocate(max_bytes);

            s.useDelimiter(delimiter);
            while (s.hasNext() && bb.hasRemaining()) {
                bb.put((byte) s.nextInt(radix));
            }

            return bb.array();
        }
    }

}
