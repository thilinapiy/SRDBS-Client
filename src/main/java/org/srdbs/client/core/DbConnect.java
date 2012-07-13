package org.srdbs.client.core;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
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

    private Connection connect() {

        Connection conn = null;
        try {

            Class.forName(Global.dbDriver).newInstance();
            Global.dbURL = "jdbc:mysql://" + Global.dbIPAddress + ":" + Global.dbPort + "/";
            conn = DriverManager.getConnection(Global.dbURL
                    + Global.dbName, Global.dbUserName, Global.dbPassword);
            logger.info("Connected to the database");
        } catch (Exception e) {
            logger.error("Database connection error : " + e);
        }

        return conn;
    }

    public void initializeDB(){

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

    public boolean updateQuery(String query) {

        Connection conn = connect();
        Statement s = null;
        try {
            s = conn.createStatement();
            s.executeUpdate(query);
            s.close();
            return true;

        } catch (Exception e) {
            logger.error("Error on update sql query : " + query);
            return false;
        }
    }
    
    public void testDbConnect() throws Exception {
        
        Connection con = connect();
        con.close();
        
    }
}
