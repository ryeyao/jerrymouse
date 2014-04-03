package cn.iie.gateway;

import com.google.gson.JsonObject;
import cn.iie.abstracthandler.CommandHandler;
import cn.iie.abstracthandler.PreProcessor;
import cn.iie.gateway.util.ConfigurationFile;
import cn.iie.gateway.util.ResourceDef2Json;
import cn.iie.gateway.util.XML2ResourceDef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import wshare.dc.DC;
import wshare.dc.ResourceInfo;
import wshare.dc.resource.*;
import wshare.dc.session.ResourceInfoImpl;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Rye on 2/21/14.
 */
public class Gateway {

    static final Logger logger = LogManager.getLogger(Gateway.class.getName());

    public static Properties idmap;
    public static String IDMAP_FILE = "idmap.ini";
    public static String XML_DIR = "xmls";
    public static String RES_DEF_DIR = "resources";

    private HashMap<String, Property> handlers = new HashMap<String, Property>();
    private ArrayList<Resource> resources = new ArrayList<Resource>();

    private Class<CommandHandler> commandHandlerClass;
    private Class<PreProcessor> preprocessorClass;

    public Properties loadConfiguration() {
        ConfigurationFile cf = new ConfigurationFile();
        Properties config = cf.loadConfiguration();

        if (config == null) {
            logger.info("Configuration file not found, recreating...");
            config = cf.initDefaultConfiguration();
            cf.updateFile(config);
        }

        return config;
    }

    private boolean init() throws IOException, ParserConfigurationException, SAXException, InstantiationException, IllegalAccessException {

        Properties config = loadConfiguration();

        if(!config.containsKey("server.host") || !config.containsKey("server.port")
                || !config.containsKey("client.commandhandler")
                || !config.containsKey("client.preprocessor")) {
            logger.error("property [server.host], [server.port], [client.commandhandler] and [client.preprocessor] must be specified.");
        }

        DC.getConfiguration().setProperty("server.host", config.getProperty("server.host"));
        DC.getConfiguration().setProperty("server.port", config.getProperty("server.port"));

        // load class
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            commandHandlerClass = (Class<CommandHandler>)classLoader.loadClass(config.getProperty("client.commandhandler"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            preprocessorClass = (Class<PreProcessor>)classLoader.loadClass(config.getProperty("client.preprocessor"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        while (!initializeResource()) {
            idmap = registerAllResources(XML_DIR);

            ConfigurationFile cf = new ConfigurationFile();
            // update idmap
            cf.updateFile(idmap, IDMAP_FILE);
        }
        return true;
    }

    private Resource registerResource(ResourceDefinition def) throws IOException {
        ResourceLibrary lib = DC.newSession(null);
        String rid = lib.addResource(def, null);
        Resource res = lib.getResource(rid);

        return res;
    }

    private Properties registerAllResources(String xmlDir) throws ParserConfigurationException, IOException, SAXException {
        // TODO: Register resource while idmap doesn't exist so that we can get a new resource id.

        // Get resource definition from xml
        File xmlDirFile = new File(xmlDir);
        XML2ResourceDef xml2r = new XML2ResourceDef();
        Properties loc2rem = new Properties();
        Properties rem2loc = new Properties();

        for (File xmlFile : xmlDirFile.listFiles()) {
            String xmlPath = xmlFile.getPath();
            ResourceDefinition def = xml2r.parse(xmlPath);

            //FIXME(Rye): Fake location
            Random rand = new Random(System.currentTimeMillis());
            double yMax = 58, yMin = -38, xMax = 170, xMin = -2;
            double x = rand.nextDouble() * (xMax - xMin) + xMin;
            double y = rand.nextDouble() * (yMax - yMin) + yMin;
//            def.description.put("geo", x + "," + y);
//            def.description.put("geo", "20,50");


            Resource res = registerResource(def);
            resources.add(res);

            String localid = def.description.get("localid");
            loc2rem.setProperty(localid, res.getId());
            rem2loc.setProperty(res.getId(), localid);

            // Generate json definition
            JsonObject jo = ResourceDef2Json.createJson(res);
//            jo.add("handlers", new GsonBuilder().create().toJsonTree(hdlrs));
            jo.addProperty("lastModified", new Date().toString());
            ResourceDef2Json.writeJson(RES_DEF_DIR + File.separator + "Component." + localid + ".json", jo);
        }

        return loc2rem;
    }

    private boolean initializeResource() throws IOException, ParserConfigurationException, SAXException, IllegalAccessException, InstantiationException {

        logger.info("Initializing resources...");
        idmap = getIDMap();
        while (idmap == null) {
            logger.info("IDMap not found, register all resources and creating a new map file...");
            idmap = registerAllResources(XML_DIR);

            // update idmap
            ConfigurationFile.updateFile(idmap, IDMAP_FILE);
        }

        // Set up all resources and control handlers if any.
        for(Object localid : idmap.keySet()) {
            String resid = (String)idmap.get(localid);
            ResourceInfo ri = new ResourceInfoImpl(resid, null);
            Resource res = DC.getResource(ri);

            // FIXME(Rye): Just test DeltResourceDefinition
            res.setDefinition(DefinitionHelper.delta(res.getDefinition()));

            if (res == null) {
                logger.info("Resource not found, delete idmap.ini and try again...");
                return false;
            }

            CommandHandler commandHandler = commandHandlerClass.newInstance();
            commandHandler.setRes(res);
            commandHandler.init();
//            DataHandler cmdHandler = new SensorCMDHandler();

            HashMap<String, String> hdlrs = new HashMap<String, String>();
            for (String ctrlPropID : res.getDefinition().relationship.keySet()) {
                Property p = res.getProperty(ctrlPropID);
                String cmdh = p.registerReader(commandHandler, null);
                this.handlers.put(cmdh, p);
                hdlrs.put(ctrlPropID, commandHandler.getClass().getCanonicalName());
            }

        }

        return true;
    }

    public static Properties getIDMap() {
        ConfigurationFile cf = new ConfigurationFile();
        Properties map = cf.loadConfiguration(IDMAP_FILE);

        return map;
    }


    private void cleanAndExit() {
        logger.info("Clean and exit.");
        for(String hdlr : handlers.keySet()) {
            Property cp = handlers.get(hdlr);
            cp.unregisterReader(hdlr);
        }
        resources = null;
//        System.exit(-1);
    }
    public void start() throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(!init()) {
            logger.error("Initialization failed.");
            return;
        }

        logger.info("Loading preProcessor...");
        PreProcessor preProcessor = preprocessorClass.newInstance();
        preProcessor.prepare();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
//                cleanAndExit();
            }
        }));
//        Signal.handle(new Signal("INT"), new SignalHandler() {
//            public void handle(Signal arg) {
//                cleanAndExit();
//            }
//        });
//        Signal.handle(new Signal("TERM"), new SignalHandler() {
//            public void handle(Signal arg) {
//                cleanAndExit();
//            }
//        });
        logger.info("Started.");
    }


}
