package ws.gw;

import ws.gw.abstracthandler.Preprocessor;
import ws.gw.udp.UDPDataHandler;
import ws.gw.udp.UDPServer;

import java.net.SocketException;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 3/27/14
 * Time: 4:30 PM
 */
public class SensorPreprocessor implements Preprocessor {
    @Override
    public void prepare() {
        try {
            startDataCollector();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void startDataCollector() throws SocketException {
        UDPServer udpServer = new UDPServer();
        udpServer.init();

        UDPDataHandler udpHandler = new UDPDataHandler();

        udpServer.setDataHandler(udpHandler);
        udpServer.run();
    }
}
