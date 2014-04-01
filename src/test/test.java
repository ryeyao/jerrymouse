package test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by Rye on 2/28/14.
 */
public class test {
    public static void main(String args[]) throws IOException {
//        DatagramSocket ds = new DatagramSocket();
//        String msg = new String("打开展灯");
//        ByteBuffer bb = ByteBuffer.allocate(8 + msg.getBytes("gb2312").length);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        int length = msg.getBytes("gb2312").length;
//        bb.putLong(201L);
//        bb.put(msg.getBytes("gb2312"));
//
//        DatagramPacket dp = new DatagramPacket(bb.array(), bb.array().length, new InetSocketAddress("192.168.110.225", 50003));
//        ds.send(dp);
//        testByte();
//        testJson();
//        System.out.println(test.class.getCanonicalName());
//        CameraClient cc = new CameraClient();
//        cc.setup();
//        cc.registerAll();
        String cmd = "8142";
        byte[] cmdb = cmd.getBytes();
        byte[] b = {0x08, 0x01, 0x04, 0x02};

        Socket socket = new Socket("192.168.111.241", 7000);

        OutputStream os = socket.getOutputStream();
        os.write(b);
        os.flush();
        os.close();
    }

//    public static void testJson() {
//        ResourceDefinition rd = CameraClient.camDefinition("1");
//        ResourceDef2Json rd2j = new ResourceDef2Json();
////        rd2j.createJson(rd);
//    }

    public static void testByte() {
//        byte[] b = {50, 56, 46, 49, 49, 0, 0, 0};
        byte[] b = "88.08   ".getBytes();
        ByteBuffer bb = ByteBuffer.wrap(b);

        int i = bb.getInt();
    }
}
