package org.jerrymouse.commons.zip;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;

/**
 * User: rye
 * Date: 12/16/14
 * Time: 13:57
 */
public class Zip {

    private static final Logger log = LogManager.getLogger(Zip.class);

    private ZipParameters parameters = null;

    public Zip() {
        parameters = new ZipParameters();

        //Deflate compression or store(no compression) can be set below
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

        // Set the compression level. This value has to be in between 0 to 9
        // Several predefined compression levels are available
        // DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
        // DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
        // DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
        // DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
        // DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
    }

    public void compress(OutputStream os, File file) {
        // TODO

    }

    public void compress(OutputStream os, List<File> files, String encrypt_key) {

        //This flag defines if the files have to be encrypted.
        //If this flag is set to false, setEncryptionMethod, as described below,
        //will be ignored and the files won't be encrypted
        parameters.setEncryptFiles(true);

        //Set encryption method to Standard Encryption
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);

        //self descriptive
        parameters.setPassword(encrypt_key);

        compress(os, files);
    }

    public void compress(OutputStream os, List<File> files) {


        try {
            try (ZipOutputStream zos = new ZipOutputStream(os);) {

                //Now we loop through each file, determine the file CRC and set it
                //in the zip parameters and then we read the input stream and write it
                //to the outputstream
                for (int i = 0; i < files.size(); i++) {

                    File file = files.get(i);
                    log.debug("Compress file [{}].", file.getName());

                    zos.putNextEntry(file, parameters);

                    //If this file is a directory, then no further processing is required
                    //and we close the entry (Please note that we do not close the outputstream yet)
                    if (file.isDirectory()) {
                        log.error("File [{}] is a directory.");
                        zos.closeEntry();
                        continue;
                    }

                    //Initialize inputstream
                    try (InputStream inputStream = new FileInputStream(file);) {
                        byte[] readBuff = new byte[4096];
                        int readLen = -1;

                        //Read the file content and write it to the OutputStream
                        while ((readLen = inputStream.read(readBuff)) != -1) {
                            zos.write(readBuff, 0, readLen);
                        }
                    }

                    //Once the content of the file is copied, this entry to the zip file
                    //needs to be closed. ZipOutputStream updates necessary header information
                    //for this file in this step
                    zos.closeEntry();
                }

                //ZipOutputStream now writes zip header information to the zip file
                zos.finish();

            }
        } catch (FileNotFoundException e) {
            log.error("Compress failed: {}", e.getMessage());
        } catch (ZipException e) {
            log.error("Compress failed: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Compress failed: {}", e.getMessage());
        }
    }
}
