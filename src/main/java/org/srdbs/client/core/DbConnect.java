package org.srdbs.client.core;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/11/12
 * Time: 9:41 AM
 * For more details visit : http://www.thilina.org
 */
public class DbConnect {

    public static Logger logger = Logger.getLogger("systemsLog");

    private Connection connect() throws Exception {

        Class.forName(Global.dbDriver).newInstance();
        Global.dbURL = "jdbc:mysql://" + Global.dbIPAddress + ":" + Global.dbPort + "/";
        Connection conn = DriverManager.getConnection(Global.dbURL
                + Global.dbName, Global.dbUserName, Global.dbPassword);
        logger.info("Connected to the database");
        return conn;
    }

    public void initializeDB() throws Exception {

        DbConnect dbCon = new DbConnect();
        dbCon.updateQuery("DROP TABLE IF EXISTS SP_FILE");
        dbCon.updateQuery("DROP TABLE IF EXISTS FULL_FILE");
        dbCon.updateQuery("CREATE TABLE FULL_FILE(" +
                "F_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "FNAME VARCHAR(100)," +
                "FSIZE BIGINT," +
                "HASHVALUE VARCHAR(100)," +
                "UP_DATE VARCHAR(100))");
        dbCon.updateQuery("CREATE TABLE SP_FILE(" +
                "SP_FILE_ID INT NOT NULL AUTO_INCREMENT," +
                "F_ID INT," +
                "SP_FILENAME VARCHAR(400)," +
                "F_SIZE BIGINT," +
                "HASHVALUE VARCHAR(200)," +
                "REF_CLOUD_ID INT," +
                "RAID_REF INT," +
                "REMOTE_PATH VARCHAR(400)," +
                "CONSTRAINT PK_SP_FILEID_1 PRIMARY KEY(SP_FILE_ID)," +
                "CONSTRAINT FK_SP_FILEID_2 FOREIGN KEY (F_ID) REFERENCES FULL_FILE (F_ID))");
        //dbCon.updateQuery("");
    }

    public int updateQuery(String query) throws Exception {

        Connection conn = connect();
        Statement s = conn.createStatement();
        int ret = s.executeUpdate(query);
        s.close();
        return ret;
    }

    public void testDbConnect() throws Exception {

        Connection con = connect();
        con.close();

    }

    public void insertToFullFile(int fid, String fileName, long fileSize,
                                 String hashValue, String upDate) throws Exception {

        String sql = "INSERT INTO FULL_FILE (F_ID,FNAME,FSIZE,HASHVALUE,UP_DATE) VALUES (?,?,?,?,?)" +
                "ON DUPLICATE KEY UPDATE F_ID = ?, FNAME = ?,FSIZE = ?, HASHVALUE =? ,UP_DATE = ?";
        Connection connection = connect();
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, fid);
        ps.setString(2, fileName);
        ps.setLong(3, fileSize);
        ps.setString(4, hashValue);
        ps.setString(5, upDate);
        ps.setInt(6, fid);
        ps.setString(7, fileName);
        ps.setLong(8, fileSize);
        ps.setString(9, hashValue);
        ps.setString(10, upDate);

        ps.addBatch();
        ps.executeBatch();
        ps.close();
        connection.close();

    }

    public void insertToSpFile(int spFId, int fid, String spFileName, long fileSize,
                               String hashValue, int refCloudId, int raidRef, String remPath) throws Exception {

        String sql = "INSERT INTO SP_FILE (SP_FILE_ID, F_ID, SP_FILENAME, F_SIZE, HASHVALUE, REF_CLOUD_ID, RAID_REF,"
                + "REMOTE_PATH ) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY  UPDATE SP_FILE_ID = ?, F_ID = ?,"
                + "SP_FILENAME = ?, F_SIZE = ?, HASHVALUE =? ,REF_CLOUD_ID = ?, RAID_REF = ?, REMOTE_PATH = ?";

        Connection connection = connect();
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, spFId);
        ps.setInt(2, fid);
        ps.setString(3, spFileName);
        ps.setLong(4, fileSize);
        ps.setString(5, hashValue);
        ps.setInt(6, refCloudId);
        ps.setInt(7, raidRef);
        ps.setString(8, remPath);
        //
        ps.setInt(9, spFId);
        ps.setInt(10, fid);
        ps.setString(11, spFileName);
        ps.setLong(12, fileSize);
        ps.setString(13, hashValue);
        ps.setInt(14, refCloudId);
        ps.setInt(15, raidRef);
        ps.setString(16, remPath);

        ps.addBatch();
        ps.executeBatch();
        ps.close();
        connection.close();

    }

    public void deleteFileDataFromDB(int fid) throws Exception {

        String spSql = "DELETE FROM SP_FILE WHERE F_ID = ?";
        String fullSql = "DELETE FROM SP_FILE WHERE F_ID = ?";

        Connection connection = connect();

        PreparedStatement ps = connection.prepareStatement(spSql);
        ps.setLong(1, fid);
        ps.executeUpdate();
        ps.close();

        PreparedStatement ps2 = connection.prepareStatement(fullSql);
        ps2.setLong(1, fid);
        ps2.executeUpdate();
        ps2.close();

        connection.close();
    }
}
