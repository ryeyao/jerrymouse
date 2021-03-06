package org.omg.gaia.startup;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.omg.gaia.Globals;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/1/14
 * Time: 9:25 AM
 */
public class Bootstrap {
    static {
        System.out.println("Current classpath: " + System.getProperty("java.class.path"));
//        System.out.println("Current lib path: " + System.getProperty("java.library.path"));
    }
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    private void init() {

        setGaiaHome();
        setGaiaBase();

    }

    public void start() {
        Gaia gaia = new Gaia();
        gaia.start();
    }

    public void setGaiaHome(String s) {
        System.setProperty(Globals.GAIA_HOME_PROP, s);
    }

    public void setGaiaBase(String s) {
        System.setProperty(Globals.GAIA_BASE_PROP, s);
    }

    private void setGaiaBase() {
        if(System.getProperty(Globals.GAIA_BASE_PROP) != null) {
            return;
        }

        if(System.getProperty(Globals.GAIA_HOME_PROP) != null) {
            System.setProperty(Globals.GAIA_BASE_PROP, System.getProperty(Globals.GAIA_HOME_PROP));
        } else {
            System.setProperty(Globals.GAIA_BASE_PROP, System.getProperty("user.dir"));
        }
    }
    private void setGaiaHome() {
        if(System.getProperty(Globals.GAIA_HOME_PROP) != null) {
            return;
        }

        File bootstrapJar = new File(System.getProperty("user.dir"), "bootstrap.jar");
        if(bootstrapJar.exists()) {
            try {
                System.setProperty(Globals.GAIA_HOME_PROP, (new File(System.getProperty("user.dir"), "")).getCanonicalPath());

            } catch (Exception e) {
                System.setProperty(Globals.GAIA_HOME_PROP, System.getProperty("user.dir"));
            }
        } else {
            System.setProperty(Globals.GAIA_HOME_PROP, System.getProperty("user.dir"));
        }
    }

    public static String getGaiaHome() {
        return System.getProperty(Globals.GAIA_HOME_PROP, System.getProperty("user.dir"));
    }

    public static String getGaiaBase() {
        return System.getProperty(Globals.GAIA_BASE_PROP, getGaiaHome());
    }

    public static void main(String[] args) {
        Bootstrap boot = new Bootstrap();
        boot.init();
        boot.start();
    }
}
