package cn.camera;

import org.xml.sax.SAXException;
import cn.iie.gateway.Gateway;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 3/28/14
 * Time: 9:06 AM
 */
public class Main {
    public static void main(String args[]) throws SAXException, IllegalAccessException, IOException, InstantiationException, ParserConfigurationException, ClassNotFoundException {
        Gateway snc = new Gateway();
        snc.start();
    }
}
