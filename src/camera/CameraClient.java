package camera;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import ws.gw.CameraCMDHandler;
import ws.gw.util.ResourceDef2Json;
import wshare.common.ClientInfo;
import wshare.dc.DC;
import wshare.dc.ResourceInfo;
import wshare.dc.resource.*;
import wshare.dc.session.ResourceInfoImpl;

import java.io.IOException;
import java.util.*;

/**
 * Created by Rye on 2/19/14.
 */
public class CameraClient {

    static final Logger logger = LogManager.getLogger(CameraClient.class.getName());
    private static String IDMAP_FILE = "conf/idmap.ini";

    public Properties loadConfiguration() {

        ConfigurationFile cf = new ConfigurationFile();
        Properties config = cf.loadConfiguration();

        if (config == null) {
            logger.info("File does not exist, recreating...");
            config = cf.initConfiguration();
            cf.updateFile(config);
        }

        return config;
    }

    public String setup() {

        Properties config = loadConfiguration();

        DC.getConfiguration().setProperty("server.host", config.getProperty("server.host"));
        DC.getConfiguration().setProperty("server.port", config.getProperty("server.port"));

        return config.getProperty("client.cameraid");
    }

    public void updateCameraConfiguration(Resource res) {
        logger.info("update configuration");
        Properties config = loadConfiguration();
        config.setProperty("client.cameraid", res.getId());

        ConfigurationFile cf = new ConfigurationFile();
        cf.updateFile(config);
    }

    public Resource register(String localid) throws IOException {
        logger.info("Register resource for [" + localid + "].");

        ResourceDefinition def = camDefinition(localid);
        ResourceLibrary lib = DC.newSession((ClientInfo) null);
        String rid = lib.addResource(def, null);

        Resource res = lib.getResource(rid);

        return res;
    }

    public ArrayList<Resource> registerAll() throws IOException {

        ArrayList<Resource> resList = new ArrayList<Resource>();
        for(int i = 1; i <= 12; i++) {
            Resource res = register(String.valueOf(i));
            resList.add(res);
            JsonObject jo = ResourceDef2Json.createJson(res);
            ResourceDef2Json.writeJson("resources/Device." + res.getDefinition().description.get("localid") + ".json", jo);
        }
        return resList;
    }

    public static void main(String[] args) throws IOException {

        CameraClient cc = new CameraClient();
        String resid = cc.setup();
//        String check = "r1-check";

        ResourceInfo ri = new ResourceInfoImpl(resid, null);
        Resource res = DC.getResource(ri);

        if (res == null) {
            // resource not found,
            // register again and store new resource id into properties file.

            try {
                res = cc.register(resid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cc.updateCameraConfiguration(res);

        }

        Property pData = res.getProperty("1");
        DataItem data = new DataItem(new Date(), "rtmp://192.168.110.221/live/mystream".getBytes());
        pData.write(data);

        final Property pCtrl = res.getProperty("2");
        DataHandler cmdHandler = new CameraCMDHandler();
        ((CameraCMDHandler) cmdHandler).setRes(res);

        final String dhId = pCtrl.registerReader(cmdHandler, null);

        // 假设程序到这将要退出，需要做释放相应的资源。
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info("Clean and exit.");
                pCtrl.unregisterReader(dhId);
            }
        }));
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal arg) {
                logger.info("Clean and exit.");
                pCtrl.unregisterReader(dhId);
            }
        });
        Signal.handle(new Signal("TERM"), new SignalHandler() {
            public void handle(Signal arg) {
                logger.info("Clean and exit.");
                pCtrl.unregisterReader(dhId);
            }
        });
        Signal.handle(new Signal("ABRT"), new SignalHandler() {
            public void handle(Signal arg) {
                logger.info("Clean and exit.");
                pCtrl.unregisterReader(dhId);
            }
        });
//        System.out.println("Unregister.");
//        pCtrl.unregisterReader(dhId); // 将释放命令处理器，不再侦听命令。

//        pCtrl = null; // 将会释放得到的属性对象
//        r = null; // 将会释放得到的资源对象
//        System.gc(); // 将彻底将释放的对象从内存清除。
    }

    public static ResourceDefinition camDefinition(String localid) {

        ResourceDefinition def = new ResourceDefinition();
        def.name = "Camera";

        def.tags = new HashSet<String>();
        def.tags.add("Camera");
        def.tags.add("controllable");
        def.tags.add("adjustable");

        def.description = new HashMap<String, String>();
        def.description.put("manufacturer", "cn.iie");
        def.description.put("birthdate", "201308");

        if(localid == "1") {
            def.description.put("localaddr", "159.226.94.34");
        }
        else if(localid == "2") {
            def.description.put("localaddr", "192.168.111.240");
        }
        else if(localid == "3") {
            def.description.put("localaddr", "192.168.111.242");
        }
        else if(localid == "4") {
            def.description.put("localaddr", "192.168.119.221");
        }
        else if(localid == "5") {
            def.description.put("localaddr", "192.168.119.223");
        }
        else if(localid == "6") {
            def.description.put("localaddr", "192.168.119.225");
        }
        else if(localid == "7") {
            def.description.put("localaddr", "192.168.110.221");
        }
        else if(localid == "8") {
            def.description.put("localaddr", "192.168.111.241");
        }
        else if(localid == "9") {
            def.description.put("localaddr", "192.168.119.220");
        }
        else if(localid == "10") {
            def.description.put("localaddr", "192.168.119.222");
        }
        else if(localid == "11") {
            def.description.put("localaddr", "192.168.119.224");
        }
        def.description.put("localport", "7000");

        PropertyDefinition pdef = new PropertyDefinition();
        pdef.name = "video";
        pdef.direction = Direction.RES_2_USER;
        pdef.dynamic = true;
        pdef.description = new HashMap<String, String>();
        pdef.description.put("comment", "provide rtmp video stream");
        pdef.type = new DataType();
        pdef.type.name = "stream";
        pdef.type.org = "cn.iie";
        pdef.type.protocol = "rtmp";
        def.properties.put("1", pdef);

        PropertyDefinition ctrlDef = new PropertyDefinition();
        ctrlDef.name = "rotate";
        ctrlDef.direction = Direction.USER_2_RES;
        ctrlDef.dynamic = true;
        ctrlDef.description = new HashMap<String, String>();
        ctrlDef.description.put("comment", "change direction of the camera");
        ctrlDef.type = new DataType();
        ctrlDef.type.name = "movement";
        ctrlDef.type.org = "cn.iie";
        ctrlDef.type.protocol = "LEFT/RIGHT/UP/DOWN";
        def.properties.put("2", ctrlDef);

        return def;
    }

    private Properties getIDMap() throws IOException {

        ConfigurationFile cf = new ConfigurationFile();
        Properties map = cf.loadConfiguration(IDMAP_FILE);

        while (map == null) {
            logger.info("IDMap not found, register all resources and creating a new map file...");
//            map = registerAll();

            // update idmap
//            cf.updateFile(map, IDMAP_FILE);
        }
        return map;
    }
}
