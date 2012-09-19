package org.srdbs.client.Messenger;

import org.apache.log4j.Logger;
import org.srdbs.client.core.DbConnect;
import org.srdbs.client.core.Global;
import org.srdbs.client.split.MYSpFile;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/17/12
 * Time: 8:47 AM
 * For more details visit : http://www.thilina.org
 */
public class MessageHandler {

    public static Logger logger = Logger.getLogger("systemsLog");
    public static String Folder;

    public static String handleInit() {

        try {
            new DbConnect().initializeDB();
            return "Database initialized successfully.";
        } catch (Exception e) {

            return "Error in database initialization.";
        }
    }

    public static String handleUpload(String type, int nRows, String data) {

        String msg = "Upload handler : ";
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

        } else if (type.equalsIgnoreCase("sp")) {

            msg = "SP_FILE SP_FID : ";

            String[] rows;
            String delimiter = ";";
            rows = data.split(delimiter);

            for (int i = 0; i < nRows; i++) {

                String[] temp;
                delimiter = ",";
                temp = rows[i].split(delimiter);

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
                    msg += spFId + ", ";

                } catch (Exception e) {
                    msg += " Error on : " + spFId;
                    logger.error("Error inserting SP_FID : " + e);
                }
            }
            logger.info(msg);
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
        DbConnect dbConnect = new DbConnect();

        try {

            List<MYSpFile> listofloadsp = dbConnect.LoadSpQueryRemotePath(fid);
            for (MYSpFile myspfile : listofloadsp) {

                Folder = myspfile.getRemotePath();
            }
        } catch (Exception ex) {
            logger.error("No Such Remote path");
        }

        String path = Global.ftpHome + Global.fs + Folder;
        System.out.print(path);
        try {

            List<MYSpFile> listofFiles = ReadSPFile(path);
            if (HashCheck(listofFiles, fid)) {
                msg = "Validation is Succesfull.";
                logger.info("Validation is Succesfull.");
            } else {
                msg = "Validation is not Successfull.";
                logger.error("Validation is not Successfull.");
            }
        } catch (Exception ex) {
            logger.error("No Such Remote path");
        }
        return msg;
    }

    public static boolean HashCheck(List<MYSpFile> listoffiles, int restoreFileID) throws Exception {

        boolean Check = true;
        int count =0;
        DbConnect dbconnect = new DbConnect();
        List<MYSpFile> listofFileSp = dbconnect.selectQuery(restoreFileID);
        int SplitCountFile = dbconnect.SplitFileCount(restoreFileID);

        if(SplitCountFile>count){

            for (MYSpFile myfile : listoffiles) {

                System.out.print(myfile.getName()+"from loop");

                for (MYSpFile dbfile : listofFileSp) {

                     if ((myfile.getName().equalsIgnoreCase(dbfile.getName()))){

                         if(myfile.getHash().equalsIgnoreCase(dbfile.getHash())){

                                System.out.print(dbfile.getName()+"from if");
                                Check = true;
                                logger.info("Pass : " + myfile.getName());
                                count++;

                                 } else {

                                    Check = false;
                                    logger.error("Fail : " + myfile.getName());
                                    //download fail data chunk
                                }
                     }
                }

            }
        }
        return Check;
    }

    public static boolean Download_HashCheck(List<MYSpFile> listoffiles, int restoreFileID) throws Exception {

        boolean Check = true;
        int count =0;
        DbConnect dbconnect = new DbConnect();
        List<MYSpFile> listofFileSp = dbconnect.selectQuery(restoreFileID);
        int SplitCountFile = dbconnect.SplitFileCount(restoreFileID);

        if(SplitCountFile>count){

            for (MYSpFile myfile : listoffiles) {

                System.out.print(myfile.getName()+"from loop");

                for (MYSpFile dbfile : listofFileSp) {

                    if ((myfile.getName().equalsIgnoreCase(dbfile.getName()))){

                        if(myfile.getHash().equalsIgnoreCase(dbfile.getHash())){

                            System.out.print(dbfile.getName()+"from if");
                            Check = true;
                            logger.info("Pass : " + myfile.getName());
                            count++;

                        } else {

                            Check = false;
                            logger.error("Fail : " + myfile.getName());
                            //download fail data chunk
                        }
                    }
                }

            }
        }
        return Check;
    }


    public static List<MYSpFile> ReadSPFile(String path) throws Exception {

        String Full_Path;
        String Hash, date;

        File folder = new File(path);
        List<MYSpFile> fileList = new ArrayList<MYSpFile>();
        for (File sysFile : folder.listFiles()) {
            Full_Path = path + "/" + sysFile.getName();
            Hash = getHash(Full_Path);
            MYSpFile mySPFile = new MYSpFile();
            mySPFile.setName(sysFile.getName());
            mySPFile.setHash(Hash);
            mySPFile.setFile(sysFile);
            fileList.add(mySPFile);
        }
        return fileList;
    }

    public static String getHash(String sCompletePath) {

        FileInputStream fis = null;
        MessageDigest md = null;
        String hashValue = "";
        byte[] dataBytes = new byte[1024];
        byte[] mdBytes = null;
        int nRead = 0;


        try {
            md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(sCompletePath);
            while ((nRead = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nRead);
            }
            mdBytes = md.digest();
            // convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdBytes.length; i++) {
                sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            logger.info("Digest in hex format :: " + sb.toString());
            hashValue = sb.toString();
            fis.close();
        } catch (Exception e) {
            logger.error("Error in hashing : " + e);
        }

        return hashValue;
    }
}
