package org.omg.jerrymouse.net.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.jerrymouse.commons.util.StringUtils;
import org.omg.jerrymouse.net.BaseDataItem;
import org.omg.jerrymouse.net.IDataHandler;
import org.omg.jerrymouse.net.IDataItem;
import org.omg.jerrymouse.net.IServer;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

/**
 * User: rye
 * Date: 12/4/14
 * Time: 16:16
 */
public class SimpleUDPServer implements IServer {

    public static final int MAX_BUFFER_SIZE = 1024;
    private static final Logger log = LogManager.getLogger(SimpleUDPServer.class);
    private DatagramSocket serverSocket;
    private InetAddress addr = null;

    private IDataHandler handler = null;
    private LinkedList<IDataHandler> interceptors = null;

    private volatile boolean server_started = false;
    private volatile boolean should_stop = false;

    private byte[] inBuffer;

    public SimpleUDPServer(String ip, int port) throws UnknownHostException, SocketException {
        addr = InetAddress.getByName(ip);
        serverSocket = new DatagramSocket(port, addr);
        interceptors = new LinkedList<IDataHandler>();
        inBuffer = new byte[MAX_BUFFER_SIZE];
    }

    @Override
    public boolean addInterceptor(IDataHandler dh) {
        if (server_started) {
            log.debug("Cannot add data handler while server is running.");
            return false;
        }
        interceptors.addLast(dh);
        return true;
    }

    @Override
    public boolean setDataHandler(IDataHandler dh) {
        if (server_started) {
            log.debug("Cannot add data handler while server is running.");
            return false;
        }

        handler = dh;

        return true;
    }

    @Override
    public void run() {
        while (!should_stop) {
            server_started = true;
            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            IDataItem msg = new BaseDataItem();

            try {
                serverSocket.receive(inPacket);

                if (log.isDebugEnabled()) {
                    log.debug("Incoming udp packet.\n\n\t[{}]\n", StringUtils.bytesToHexString(inPacket.getData(), inPacket.getLength()));
                } else {
                    log.info("Incoming udp packet.");
                }

                msg.setData(inPacket.getData());
                msg.setDataLength(inPacket.getLength());
                msg.setSource(inPacket.getSocketAddress());


                // Process raw
                msg = handler.process(msg);

                for (IDataHandler h : interceptors) {
                    msg = h.process(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("Server finished working.");

    }

    public void shutdown() {
        log.info("Stopping server...");
        should_stop = true;
    }

}
