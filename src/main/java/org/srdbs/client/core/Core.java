package org.srdbs.client.core;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.srdbs.client.Messenger.Messenger;
import org.srdbs.client.scheduler.Scheduler;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/10/12
 * Time: 5:25 PM
 * For more details visit : http://www.thilina.org
 */
public class Core {

    public static Logger logger = Logger.getLogger("systemsLog");
    public static Properties sysconfig = new Properties();
    public static Messenger server;

    public static void main(String[] args) {


        Global.fs = System.getProperty("file.separator");
        Global.systemHome = System.getenv("SRDBSCLIENT_HOME"); // get the current working directory
        System.out.println("System SRDBSCLIENT_HOME path is set to : " + Global.systemHome);

        if (Global.systemHome == null) {
            System.out.println("Set the environment variable \"SRDBSCLIENT_HOME\" and rerun the system.");
            System.exit(-1);
        } else {
            Global.sysConfigPath = Global.systemHome + Global.fs + "config" + Global.fs + "sysconfig.conf";
            System.out.println("System main config file path is set to : " + Global.sysConfigPath);
        }

        try {
            PropertyConfigurator.configure(Global.sysConfigPath);
            sysconfig.load(new FileInputStream(Global.sysConfigPath));
            logger.info("Logs initialization done by using system configuration file.");

            Global.ftpHome = sysconfig.getProperty("server.ftplocation");
            Global.serverPort = sysconfig.getProperty("server.port");
            Global.cloudid = sysconfig.getProperty("server.cloudid");
            Global.serverip = sysconfig.getProperty("server.domainname");

            Global.dbDriver = "com.mysql.jdbc.Driver";
            Global.dbIPAddress = sysconfig.getProperty("mysql.dbIPAddress");
            Global.dbPort = Integer.valueOf(sysconfig.getProperty("mysql.dbPort"));
            Global.dbName = sysconfig.getProperty("mysql.dbName");
            Global.dbURL = "jdbc:mysql://" + Global.dbIPAddress + ":" + Global.dbPort + "/";
            Global.dbUserName = sysconfig.getProperty("mysql.dbUserName");
            Global.dbPassword = sysconfig.getProperty("mysql.dbPassword");
        } catch (Exception e) {
            System.out.println("Cannot read the sysconfig.conf file. \n" + e);
            System.exit(-1);
        }

        try {
            new DbConnect().testDbConnect();
            logger.info("Connected to the database.");
        } catch (Exception e) {
            logger.error("Database connection error : " + e);
            System.exit(-1);
        }


        class runMessenger implements Runnable {

            @Override
            public void run() {
                server = new Messenger();
                try {
                    server.start();
                } catch (Exception e) {
                    logger.info("Error in the messenger : " + e);
                    System.exit(-1);
                }
            }
        }

        class runScheduler implements Runnable {

            @Override
            public void run() {
                new Scheduler();
            }
        }

        Thread t1 = new Thread(new runMessenger());
        Thread t2 = new Thread(new runScheduler());
        t1.start();
        t2.start();
        System.out.println("Started...");
        while (true) ;
    }

    public static void stopMessenger() {

        try {
            server.stop();
            logger.info("Stop the messenger");
        } catch (Exception e) {
            logger.error("Error when stopping the messenger : " + e);
        }
    }

    public static void stopService() {

        stopMessenger();
        logger.info("Stopping the system.");
        System.exit(0);
    }
}
