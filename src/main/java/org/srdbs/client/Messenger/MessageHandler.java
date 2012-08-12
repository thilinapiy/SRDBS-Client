package org.srdbs.client.Messenger;

import org.apache.log4j.Logger;
import org.srdbs.client.core.DbConnect;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/17/12
 * Time: 8:47 AM
 * For more details visit : http://www.thilina.org
 */
public class MessageHandler {

    public static Logger logger = Logger.getLogger("systemsLog");

    public static String handleInit() {

        try {
            new DbConnect().initializeDB();
            return "Database initialized successfully.";
        } catch (Exception e) {

            return "Error in database initialization.";
        }
    }

    public static String handleUpload(String type, String data) {

        String msg = "";
        if (type.equalsIgnoreCase("full")) {

            String[] temp;
            String delimiter = ",";
            temp = data.split(delimiter);

            int fid = Integer.valueOf(temp[0]);
            String fileName = temp[1];
            long fileSize = Long.valueOf(temp[2]);
            String hashValue = temp[3];
            String upDate = temp[4];

            try {
                //  insert into Full_File table.
                logger.info("Trying to insert Full_File data : " + fid + ", " + fileName + ", "
                        + fileSize + ", " + hashValue + ", " + upDate);
                new DbConnect().insertToFullFile(fid, fileName, fileSize, hashValue, upDate);
                msg = "Full file update received and insert to the database. FID is : " + fid;
                logger.info(msg);

            } catch (Exception e) {
                msg = "Full file update received but not insert in to database.";
                logger.error(msg + " : " + e);
            }
        }

        if (type.equalsIgnoreCase("sp")) {

            String[] temp;
            String delimiter = ",";
            temp = data.split(delimiter);

            int spFId = Integer.valueOf(temp[0]);
            int fid = Integer.valueOf(temp[1]);
            String spFileName = temp[2];
            long fileSize = Long.valueOf(temp[3]);
            String hashValue = temp[4];
            int refCloudId = Integer.valueOf(temp[5]);
            int raidRef = Integer.valueOf(temp[6]);
            String remPath = temp[7];

            try {
                new DbConnect().insertToSpFile(spFId, fid, spFileName, fileSize, hashValue, refCloudId, raidRef, remPath);
                msg = "Split file update received and insert to the database. SPFID is : " + spFId;
                logger.info(msg);

            } catch (Exception e) {
                msg = "Split file update received but not insert in to database. SPFID is : " + spFId;
                logger.error(msg + " : " + e);
            }
        }
        return msg;
    }

    public static void handleData() {

    }

    public static void handleRequest() {

    }

    public static String handleDelete(int fid) {

        String msg = "";
        try {
            new DbConnect().deleteFileDataFromDB(fid);
            msg = "Successfully delete file data from the database. FID : " + fid;
            logger.info(msg);

        } catch (Exception e) {
            msg = "Error occurred when deleting file data from the database. FID : " + fid;
            logger.error(msg + " : " + e);
        }
        return msg;
    }

    public static void handleError() {

    }

    /* This method will check the integrity of uploaded file.
     *
     *
     */
    public static String handleValidate(int fid) {

        String msg = "";
        // validate hash values of files and values from the database.


        return msg;

    }
}
