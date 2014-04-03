package cn.iie.udp;

import cn.iie.gateway.util.ConfigurationFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Properties;

/**
 * Created by Rye on 2/22/14.
 */
public class UDPClient {

    private static Logger logger = LogManager.getLogger(UDPClient.class.getName());

    private DatagramSocket clientSocket;
    private String host;
    private int port;
    private byte[] sendBuffer;
    private byte[] recvBuffer;

    public void init() throws SocketException {
        logger.info("Initializing cn.iie.udp Client...");

        ConfigurationFile cf = new ConfigurationFile();
        Properties config = cf.loadConfiguration();
        host = config.getProperty("gateway.host");
        port = Integer.valueOf(config.getProperty("gateway.port"));
//        clientSocket = new DatagramSocket(new InetSocketAddress(host, port));
        clientSocket = new DatagramSocket();

        sendBuffer = new byte[1024];
        recvBuffer = new byte[1024];
    }

    public void send(byte[] data) throws IOException {

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, new InetSocketAddress(host, port));
        clientSocket.send(sendPacket);
    }

    public void shutDown() {
        logger.info("Shutting down cn.iie.udp Client");

        clientSocket.close();
        sendBuffer = null;
        recvBuffer = null;
    }
}
