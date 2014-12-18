package org.jerrymouse.net.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.net.ftp.FtpLoginException;

import java.io.*;

/**
 * User: rye
 * Date: 12/16/14
 * Time: 13:56
 */
public class FtpUploader {

    private static final Logger log = LogManager.getLogger(FtpUploader.class);

    private String ip = null;
    private int port;
    private FTPClient client = null;


    public FtpUploader(String ip, int port) {
        this.ip = ip;
        this.port = port;
        client = new FTPClient();
    }

    public void login(String user, String passwd) {


        try {
            client.connect(ip, port);
            client.login(user, passwd);
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);

        } catch (IOException e) {
            log.error("FTP: [{}].", e.getMessage());
        }
    }

    public boolean upload(String local_path, String remote_path) {
        File local_file = new File(local_path);

        return upload(local_path, remote_path);
    }

    public boolean upload(File local_file, String remote_path) {
        boolean done = false;
        try {

            InputStream is = new FileInputStream(local_file);

            log.debug("Start uploading file [{}] to [ftp://{}:{}/{}].", local_file.getName(), ip, port, remote_path);
            done = client.storeFile(remote_path, is);

            is.close();
            if (done) {
                log.debug("File uploaded.");
            }

        } catch (FtpLoginException e) {
            log.error("Error: " + e.getMessage());
            done = false;

        } catch (FTPConnectionClosedException e) {
            log.error("Error: " + e.getMessage());
            done = false;

        } catch (FileNotFoundException e) {
            log.error("Error: " + e.getMessage());
            done = false;
        } catch (IOException e) {
            log.error("Error: " + e.getMessage());
            done = false;
        } finally {
            try {
                if (client.isConnected()) {
                    client.logout();
                    client.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return done;
        }

    }
}
