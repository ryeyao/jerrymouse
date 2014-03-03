package SensorNetwork.UDP;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Rye on 2/21/14.
 */
public class UDPPacket {

    private long type;
    private long nodeid;
    private long value;
    private String cmd;
    private byte[] packed;

    private static int DATA_PACKET_SIZE = 24;
    private static int BASIC_CMD_PACKET_SIZE = 8;

    public UDPPacket() {

    }

    public void setType(long type) {
        this.type = type;
    }

    public long getType() {
        return type;
    }

    public long getNodeid() {
        return nodeid;
    }

    public void setNodeid(long nodeid) {
        this.nodeid = nodeid;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Deprecated
    public byte[] packData() {
        ByteBuffer bb = ByteBuffer.allocate(DATA_PACKET_SIZE);
        bb.putLong(type);
        bb.putLong(nodeid);
        bb.putLong(value);

        return bb.array();
    }

    public void unpackData(byte[] raw) {
        ByteBuffer bb = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        this.type = bb.getLong();
        this.nodeid = bb.getLong();
        this.value = bb.getLong();
    }

    public byte[] packCmd() throws UnsupportedEncodingException {
        ByteBuffer bb = ByteBuffer.allocate(BASIC_CMD_PACKET_SIZE + cmd.getBytes("gb2312").length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(nodeid);
        bb.put(cmd.getBytes("gb2312"));

        return bb.array();

    }

    @Deprecated
    public void unpackCmd(byte[] raw) {
        ByteBuffer bb = ByteBuffer.wrap(raw);

        this.nodeid = bb.getLong();
        this.cmd = new String(bb.array());
    }

    @Override
    public String toString() {

        return String.format("[%d] [%d] [%s] [%s]", nodeid, type, value, cmd);
    }

}
