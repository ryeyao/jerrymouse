package test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import camera.ConfigurationFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import wshare.common.ClientInfo;
import wshare.dc.DC;
import wshare.dc.resource.DataItem;
import wshare.dc.resource.DataType;
import wshare.dc.resource.Direction;
import wshare.dc.resource.Property;
import wshare.dc.resource.PropertyDefinition;
import wshare.dc.resource.Resource;
import wshare.dc.resource.ResourceDefinition;
import wshare.dc.resource.ResourceLibrary;

public class CameraUserTest {

	private String rid;
	private ResourceLibrary lib;
    private ConfigurationFile cf;
	private long time;
	private TimeUnit unit = TimeUnit.MILLISECONDS;

	@Before
	public void setUp() throws IOException {
		time = System.nanoTime();
        cf = new ConfigurationFile();
        Properties conf = cf.loadConfiguration();
		DC.getConfiguration().setProperty("server.host", conf.getProperty("server.host"));
		DC.getConfiguration().setProperty("server.port", conf.getProperty("server.port"));
        rid = conf.getProperty("client.cameraid");
		lib = DC.newSession((ClientInfo) null);

		printTimeStamp("setup");
	}
	
	public void printTimeStamp(String title) {
		long millis = unit.convert(System.nanoTime() - time, TimeUnit.NANOSECONDS);
		System.out.printf("[%s]\t%s %s%s", title, millis, unit, System.lineSeparator());
	}

	@After
	public void tearDown() {
		lib = null;
		System.gc();
		printTimeStamp("teardown");
	}

//	@Test
	public void testAdding2() throws IOException {
		testAdding();
		tearDown();
		setUp();
		testExisting();
	}

//	@Test
	public void testAdding() throws IOException {
		ResourceDefinition def = randDefinition();
		System.out.println(def);

		rid = lib.addResource(def, null);
		System.out.println(rid);
		Resource rsc = lib.getResource(rid);

		ResourceDefinition def1 = rsc.getDefinition();
		System.out.println(def1.toString());
	}

	@Test
	public void testCmd() throws IOException {
//		rid = "396f0da5-ea63-42a1-9805-1d9919f2b591";
		Resource rsc = lib.getResource(rid);
		Property ctrl = rsc.getProperty("2");
		ctrl.write(new DataItem(new Date(), "hello wshare".getBytes()));
	}

//	@Test
	public void testData() {
//		rid = "396f0da5-ea63-42a1-9805-1d9919f2b591";
		Resource rsc = lib.getResource(rid);
		printTimeStamp("rsc");
		Property data = rsc.getProperty("1");
		printTimeStamp("property");
		DataItem item = data.readCurrent();
		printTimeStamp("data");
		System.out.println(item != null);
		System.out.println(Arrays.toString(item.data));
		System.out.println(new String(item.data));
	}

//	@Test
	public void testExisting() throws IOException {
//		String rid = "227675d4-140e-4255-ac17-f6b2f4a0943a";
		System.out.println(rid);
		Resource rsc = lib.getResource(rid);
		ResourceDefinition def1 = rsc.getDefinition();
		System.out.println(def1.toString());
	}

	private ResourceDefinition randDefinition() {
		ResourceDefinition def = new ResourceDefinition();
		def.name = "Camera";

		def.tags = new HashSet<String>();
		def.tags.add("Camera");
		def.tags.add("controllable");
		def.tags.add("adjustable");

		def.description = new HashMap<String, String>();
		def.description.put("manufacturer", "iie.iie");
		def.description.put("birthdate", "201308");

		PropertyDefinition pdef = new PropertyDefinition();
		pdef.name = "video";
		pdef.direction = Direction.RES_2_USER;
		pdef.dynamic = true;
		pdef.description = new HashMap<String, String>();
		pdef.description.put("comment", "provide rtmp video stream");
		pdef.type = new DataType();
		pdef.type.name = "stream";
		pdef.type.org = "iie.iie";
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
		ctrlDef.type.org = "iie.iie";
		ctrlDef.type.protocol = "LEFT/RIGHT/UP/DOWN";
		def.properties.put("2", ctrlDef);

		return def;
	}

}
