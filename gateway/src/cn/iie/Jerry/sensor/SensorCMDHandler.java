package cn.iie.jerry.sensor;

import cn.iie.gateway.abstracthandler.CommandHandler;
import cn.iie.jerry.sensor.udp.UDPClient;
import cn.iie.jerry.sensor.udp.UDPPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wshare.dc.resource.DataItem;
import wshare.dc.resource.PID;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Created by Rye on 2/22/14.
 */
public class SensorCMDHandler extends CommandHandler {

    private UDPClient client;
    private String localid;// Resource local id
    private HashMap typeMap;
    private static final Logger logger = LogManager.getLogger(SensorCMDHandler.class.getName());

    public void init() {
        this.localid = this.getRes().getDefinition().description.get("localid");
        client = new UDPClient();
        try {
            client.init();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        typeMap = new HashMap();
        typeMap.put(7, "温度");
        typeMap.put(6, "湿度");
        typeMap.put(4, "红外数据");
        typeMap.put(10, "展柜状态");
        typeMap.put(11, "展灯状态");
    }

    @Override
    public void handle(PID propertyId, DataItem... data) {

        try {
            logger.info("Received command: \n" + new String(propertyId.getLocalId().getBytes("utf-8"), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String propIdStr = propertyId.getLocalId();
//        long type = (long)typeMap.get(propIdStr.substring(0, propIdStr.indexOf(":")));
        String command = propIdStr.substring(propIdStr.indexOf("/") + 1);
        UDPPacket packet = new UDPPacket();

        try {
            packet.setCmd(new String(command.getBytes("utf-8"), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        packet.setNodeid(Long.parseLong(localid));

//        for (DataItem di : data) {
//            logger.info(di.data);
//            try {
//            } catch (IOException e) {
//                logger.error("UDPClient not working.");
//            }
//        }

        try {
            logger.info("Send command to Component");
            client.send(packet.packCmd());
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("UDPClient not working.");
        }
    }
}
