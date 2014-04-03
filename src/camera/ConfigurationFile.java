package camera;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Created by Rye on 2/20/14.
 */
public class ConfigurationFile {

    public static String fileName = "camera.config.properties";
    static final Logger logger = LogManager.getLogger(ConfigurationFile.class.getName());

    public void updateFile(Properties prop) {

        logger.info("update File");
        OutputStream output = null;

        try {
            output = new FileOutputStream(fileName);
            prop.store(output, null);
            output.flush();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    public Properties loadConfiguration() {
        return loadConfiguration(fileName);
    }

    public Properties loadConfiguration(String fname) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(fname);
            prop.load(input);

            if (input != null) {
                try {
                    input.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
            return prop;
        } catch (IOException io) {
//            io.printStackTrace();
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public Properties initConfiguration() {

        Properties prop = new Properties();
        prop.setProperty("server.host", "192.168.119.175");
        prop.setProperty("server.port", "8111");
        prop.setProperty("client.handler", "Component.handler.CommandHandler");

        return prop;
    }
}
