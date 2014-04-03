package cn.iie.gateway.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
import org.xml.sax.SAXException;
import wshare.dc.resource.DataType;
import wshare.dc.resource.Direction;
import wshare.dc.resource.PropertyDefinition;
import wshare.dc.resource.ResourceDefinition;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Rye on 2/22/14.
 */
public class XML2ResourceDef {

    private DocumentBuilder db;

    public XML2ResourceDef() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
    }
    public ResourceDefinition parse(String xmlPath) throws IOException, SAXException {

        ResourceDefinition def = new ResourceDefinition();

        File xmlFile = new File(xmlPath);
        Document doc = db.parse(xmlFile);

        // Optional but recommended
        doc.getDocumentElement().normalize();

        Node device = doc.getElementsByTagName("device").item(0);
        NodeList propList = device.getChildNodes();

        // Get all Component properties
        for (int i = 0; i < propList.getLength(); i++) {
            Node prop = propList.item(i);
//            if (prop.getNodeType() != Node.ELEMENT_NODE) {
//                continue;
//            }
//            Element propElement = (Element) prop;
            String nodeName = prop.getNodeName();

            if (nodeName.equalsIgnoreCase("localid")) {
                def.description = new HashMap<String, String>();
//                def.description.put("localid", prop.getFirstChild().getNodeValue());
                def.description.put("localid", prop.getTextContent());
            }
            else if (nodeName.equalsIgnoreCase("devicetype")) {
                def.name = prop.getAttributes().getNamedItem("name").getNodeValue();
                def.tags = new HashSet<String>();
                def.tags.add(def.name);
                def.tags.add("controllable");
                def.tags.add("adjustable");

                def.description.put("manufacturer", "iie.iie");
                def.description.put("birthdate", "201308");
            }
            else if (nodeName.equalsIgnoreCase("datatype")) {
            }
            else {
                def.description.put(nodeName.toLowerCase(), prop.getTextContent());
            }

        }

        // Get all datatypes
        NodeList dataTypeList = doc.getElementsByTagName("datatype");
        for(int dtn = 0, propNum = 1; dtn < dataTypeList.getLength(); dtn++, propNum += 2) {

            Node dtNode = dataTypeList.item(dtn);
//            if (dtNode.getNodeType() != Node.ELEMENT_NODE) {
//                continue;
//            }
            NodeList datatypePropList = dtNode.getChildNodes();

            PropertyDefinition propDef = new PropertyDefinition();
            propDef.name = dtNode.getAttributes().getNamedItem("name").getNodeValue();
            propDef.direction = Direction.RES_2_USER;
            propDef.dynamic = true;
            propDef.description = new HashMap<String, String>();
            propDef.type = new DataType();
            propDef.type.org = "iie.iie";


            // Process each of every datatype's childnodes
            for(int dtpn = 0; dtpn < datatypePropList.getLength(); dtpn++) {
                Node dtProp = datatypePropList.item(dtpn);
//                if (dtProp.getNodeType() != Node.ELEMENT_NODE) {
//                    continue;
//                }
//                Element dtPropElement = (Element) dtProp;
//                String dtpNodeName = dtPropElement.getNodeName();
                String dtpNodeName = dtProp.getNodeName();

                if (dtpNodeName.equalsIgnoreCase("description")) {
//                    propDef.description.put("comment", dtPropElement.getFirstChild().getNodeValue());
                    propDef.description.put("comment", dtProp.getNodeValue());
                }
                else if (dtpNodeName.equalsIgnoreCase("stream")) {
                    propDef.type.name = propDef.name;
                }
                else if (dtpNodeName.equalsIgnoreCase("unit")) {
//                    propDef.type.protocol = dtPropElement.getFirstChild().getNodeValue();
                    propDef.type.protocol = dtProp.getNodeValue();
                }
                else if (dtpNodeName.equalsIgnoreCase("command")) {
                    PropertyDefinition ctrlDef = new PropertyDefinition();
//                    ctrlDef.name = dtPropElement.getAttribute("name");
                    ctrlDef.name = dtProp.getAttributes().getNamedItem("name").getNodeValue();
                    ctrlDef.direction = Direction.USER_2_RES;
                    ctrlDef.dynamic = true;
                    ctrlDef.description = new HashMap<String, String>();
                    ctrlDef.description.put("comment", "do something");
                    ctrlDef.type = new DataType();
                    ctrlDef.type.name = "control";
                    ctrlDef.type.org = "iie.iie";
                    String key = propDef.name + "/" + ctrlDef.name;
                    def.properties.put(key, ctrlDef);
                    def.relationship.put(key, propDef.name);
                }
            }
            def.properties.put(propDef.name, propDef);
        }
        return def;
    }
}
