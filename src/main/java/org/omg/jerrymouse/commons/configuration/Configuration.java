package org.omg.jerrymouse.commons.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: rye
 * Date: 12/4/14
 * Time: 15:07
 */
public class Configuration extends Properties {

    private static final Logger log = LogManager.getLogger(Configuration.class);

    private static volatile String s_config_fp = null;
    private String config_fp = null;

    private Configuration(String file_name) {
        this.config_fp = file_name;
        loadConfigFromFile(this.config_fp);
    }

    /**
     * @param file_path must be specified at the first call.
     * @return
     */
    public static Configuration instance(String file_path) {
        if (file_path == null && s_config_fp == null) {
            return null;
        }

        if (s_config_fp == null) {
            s_config_fp = file_path;
        }

        return ConfigurationSingleton.instance;
    }

    public static Configuration instance() {
        return instance(s_config_fp);
    }

    private void loadConfigFromFile(String file_path) {
        log.info("Load configuration from [{}].", file_path);

        try {
            try (InputStream is = new FileInputStream(file_path);) {
                Properties prop = new Properties();
                prop.load(is);
                this.putAll(prop);
            }
        } catch (FileNotFoundException fnfe) {
            log.error("File [{}] not found.", file_path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addConfigsFromFiles(String[] file_paths) {

        for (String fp : file_paths) {
            loadConfigFromFile(fp);
        }
    }

    private String getProperty(String key, Object default_value) {
        return getProperty(key, String.valueOf(default_value));
    }

    public String getConfigFilePath() {
        return config_fp;
    }

    public String getString(String key) {
        return getProperty(key);
    }

    public String getString(String key, String default_value) {
        return getProperty(key, default_value);
    }

    public boolean getBoolean(String key, boolean default_value) {

        return Boolean.valueOf(getProperty(key, default_value));
    }

    public int getInt(String key, int default_value) {
        return Integer.valueOf(getProperty(key, default_value));
    }

    public float getFloat(String key, float default_value) {
        return Float.valueOf(getProperty(key, default_value));
    }

    public double getDouble(String key, double default_value) {
        return Double.valueOf(getProperty(key, default_value));
    }

    public double getDouble(String key) {
        return Double.valueOf(getProperty(key));
    }

    public long getLong(String key, int default_value) {
        return Long.valueOf(getProperty(key, default_value));
    }

    public long getLong(String key) {
        return Long.valueOf(getProperty(key));
    }

    public String[] getStringArray(String key, String[] default_value) {
        String s = getString(key, "");
        if (s != "") {
            return s.split(",");
        }

        String[] a = {};
        return a;
    }

    private static final class ConfigurationSingleton {
        private static final Configuration instance = new Configuration(s_config_fp);
    }

}
