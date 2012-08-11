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
        dbCon.updateQuery("DROP TABLE IF EXISTS sp_File");
        dbCon.updateQuery("DROP TABLE IF EXISTS full_File");
        dbCon.updateQuery("CREATE TABLE full_file(" +
                "F_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "FName VARCHAR(100)," +
                "FSize BIGINT," +
                "HashValue VARCHAR(100)," +
                "Up_Date VARCHAR(100))");
        dbCon.updateQuery("Create Table Sp_File(" +
                "SP_FILE_ID int not null auto_increment," +
                "F_ID int," +
                "SP_FileName varchar(400)," +
                "F_SIZE Bigint," +
                "HashValue varchar(200)," +
                "Ref_Cloud_ID int," +
                "Raid_Ref int," +
                "Remote_path varchar(400)," +
                "Constraint Pk_SP_FileID_1 Primary key(SP_FILE_ID)," +
                "Constraint FK_SP_FileID_2 Foreign key (F_ID) References Full_File (F_ID))");
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

        String sql = "insert into Full_file (F_ID,FName,FSize,HashValue,Up_Date) values (?,?,?,?,?)" +
                "ON DUPLICATE KEY UPDATE F_ID = ?, FName = ?,FSize = ?, HashValue =? ,Up_Date = ?";
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

        String sql = "insert into Sp_file (SP_FILE_ID, F_ID, SP_FileName, F_SIZE, HashValue, Ref_Cloud_ID, Raid_Ref,"
                + "Remote_path ) values (?,?,?,?,?,?,?,?) ON DUPLICATE KEY  UPDATE SP_FILE_ID = ?, F_ID = ?,"
                + "SP_FileName = ?, F_Size = ?, HashValue =? ,Ref_Cloud_ID = ?, Raid_Ref = ?, Remote_path = ?";

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

        String spSql = "DELETE FROM sp_file where F_ID = ?";
        String fullSql = "DELETE FROM sp_file where F_ID = ?";

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
