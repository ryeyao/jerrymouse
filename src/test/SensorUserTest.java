package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ws.gw.util.ConfigurationFile;
import wshare.dc.DC;
import wshare.dc.resource.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rye on 2/24/14.
 */
@RunWith(Parameterized.class)
public class SensorUserTest {

    private static ResourceLibrary lib;
    private static ConfigurationFile cf;
    private static Properties idmap;
    private static ArrayList<Resource> resList = new ArrayList<Resource>();
    private static ArrayList<Property> ctrlPropList = new ArrayList<Property>();
    private long time;
    private static TimeUnit tunit = TimeUnit.MILLISECONDS;

    private Property ctrlProp;

    public SensorUserTest(Property ctrlProp) {
        this.ctrlProp = ctrlProp;
        time = System.nanoTime();
    }

//    @BeforeClass
    public static void init() throws IOException {
        cf = new ConfigurationFile();
        Properties conf = cf.loadConfiguration();
        idmap = cf.loadConfiguration("idmap.ini");

        DC.getConfiguration().setProperty("server.host", conf.getProperty("server.host"));
        DC.getConfiguration().setProperty("server.port", conf.getProperty("server.port"));
        lib = DC.newSession(null);

        System.out.println("Retrieving resources and controllable properties...");
        for (Object ri : idmap.values()) {
            String resid = (String) ri;
            Resource res = lib.getResource(resid);
            resList.add(res);
            ResourceDefinition resdef = res.getDefinition();
            for (String cpid : resdef.relationship.keySet()) {
                Property ctrlProp = res.getProperty(cpid);
                ctrlPropList.add(ctrlProp);
            }
        }

    }


    @Parameterized.Parameters
    public static Collection prepareParams() throws IOException {
        init();
        Property[][] propParams = new Property[ctrlPropList.size() * 10][1];
        for (int i = 0; i < propParams.length; i++) {
            propParams[i][0] = ctrlPropList.get(i%ctrlPropList.size());
        }
        return Arrays.asList(propParams);
    }

    public void printTimeStamp(String title) {
        long millis = tunit.convert(System.nanoTime() - time, TimeUnit.NANOSECONDS);
        System.out.printf("[%s]\t%s %s%s", title, millis, tunit, System.lineSeparator());
    }

    @Test
    public void testCMD2Sensor() {
        System.out.printf("Test contrl prop [%s] for [%s]\n", this.ctrlProp.getId().getLocalId(), this.ctrlProp.getResource().getId());
        this.ctrlProp.write(new DataItem(new Date(), this.ctrlProp.getId().getLocalId().getBytes()));
        printTimeStamp("CMD2Sensor");
    }
}

