package cn.iie.gaia.startup;

import cn.iie.gaia.*;
import cn.iie.gaia.entity.StandardContainer;
import cn.iie.gaia.loader.ComponentClassLoader;
import cn.iie.gaia.loader.ComponentLoader;
import cn.iie.gaia.entity.ContainerBase;
import cn.iie.gaia.util.ExceptionUtils;
import cn.iie.gaia.util.StringManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

import javax.naming.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/4/14
 * Time: 6:33 PM
 */
public class Gaia {

    private static final Logger log = LogManager.getLogger(Gaia.class);
    private ArrayList<Component> components = new ArrayList<Component>();
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    protected String configFile = "conf/server.properties";
    protected boolean useShutdownHook = true;
    protected Thread shutdownHook = null;

    public void setConfigFile(String file) {
        configFile = file;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setUseShutdownHook(boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }

    public boolean getUseShutdownHook() {
        return useShutdownHook;
    }

    protected File configFile() {
        File file = new File(configFile);
        if(!file.isAbsolute()) {
            file = new File(System.getProperty(Globals.GAIA_BASE_PROP), configFile);
        }

        return file;
    }

    /**
     * Initialize all components
     */
    public void load() {
        long t1 = System.nanoTime();

        initDirs();

        InputSource inputSource = null;
        InputStream inputStream = null;
        File file = null;

        try {
            file = configFile();
            inputStream = new FileInputStream(file);
            inputSource = new InputSource(file.toURI().toURL().toString());
        } catch (Exception e) {
            log.error(sm.getString("gaia.configFail", file), e);
        }

        if(inputStream == null) {
            try {
                inputStream = getClass().getClassLoader().getResourceAsStream(getConfigFile());
                inputSource = new InputSource(getClass().getClassLoader().getResource(getConfigFile()).toString());
            } catch (Exception e) {
                log.error(sm.getString("gaia.configFail", getConfigFile()), e);
            }
        }

        // load components from user component dir

        try {
            loadAllComponentes();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        long t2 = System.nanoTime();

        log.info("Initialization processed in " + ((t2 - t1) / 1000000) + " ms");
    }

    private void loadAllComponentes() throws MalformedURLException {
        String componentHome = GaiaProperties.getProperty("component.home");
        Container rootContainer = new StandardContainer();
        String root = System.getProperty(Globals.GAIA_HOME_PROP) + File.separatorChar + componentHome;
        rootContainer.setName(root);
        rootContainer.setParentClassLoader(Gaia.class.getClassLoader());

        File rootDir = new File(rootContainer.getPath());

        assert rootDir.isDirectory();

        for(File compDir : rootDir.listFiles()) {
            Container compContainer = new StandardContainer();
            compContainer.setName(compDir.getName());
            compContainer.setParent(rootContainer);

            ComponentLoader compLoader = new ComponentLoader();
            compLoader.setContainer(compContainer);
            compLoader.addRepository(new File(compDir.getAbsolutePath() + File.separatorChar + Component.CLASS_DIR_NAME).toURI().toString());

            try {
                compLoader.start();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }

    }

    private void startAllComponents() {
        for(Component c: components) {
            try {
                c.init();
                c.start();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        load();
        startAllComponents();

        if(useShutdownHook) {
            if(shutdownHook == null) {
                shutdownHook = new GaiaShutdownHook();
            }
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }

    protected void usage() {
        System.out.println(
                "usage: ./startup "
        );
    }
    protected void initDirs() {

        String gaiaHome = System.getProperty(Globals.GAIA_HOME_PROP);
        if (gaiaHome == null) {
            // Backwards compatibility patch for J2EE RI 1.3
            String j2eeHome = System.getProperty("com.sun.enterprise.home");
            if (j2eeHome != null) {
                gaiaHome=System.getProperty("com.sun.enterprise.home");
            } else if (System.getProperty(Globals.GAIA_BASE_PROP) != null) {
                gaiaHome = System.getProperty(Globals.GAIA_BASE_PROP);
            }
        }
        // last resort - for minimal/embedded cases.
        if(gaiaHome==null) {
            gaiaHome=System.getProperty("user.dir");
        }
        if (gaiaHome != null) {
            File home = new File(gaiaHome);
            if (!home.isAbsolute()) {
                try {
                    gaiaHome = home.getCanonicalPath();
                } catch (IOException e) {
                    gaiaHome = home.getAbsolutePath();
                }
            }
            System.setProperty(Globals.GAIA_HOME_PROP, gaiaHome);
        }

        if (System.getProperty(Globals.GAIA_BASE_PROP) == null) {
            System.setProperty(Globals.GAIA_BASE_PROP,
                    gaiaHome);
        } else {
            String gaiaBase = System.getProperty(Globals.GAIA_BASE_PROP);
            File base = new File(gaiaBase);
            if (!base.isAbsolute()) {
                try {
                    gaiaBase = base.getCanonicalPath();
                } catch (IOException e) {
                    gaiaBase = base.getAbsolutePath();
                }
            }
            System.setProperty(Globals.GAIA_BASE_PROP, gaiaBase);
        }

    }

    public void stop() {
        try {
            if(useShutdownHook) {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);

        }



    }

    protected class GaiaShutdownHook extends Thread {

        @Override
        public void run() {
            try {
                Gaia.this.stop();
            } catch (Throwable ex) {
                ExceptionUtils.handleThrowable(ex);
                log.error(sm.getString("gaia.shutdownHookFail"), ex);
            } finally {
                // If JULI is used, shut JULI down *after* the server shuts down
                // so log messages aren't lost
            }
        }
    }


}
