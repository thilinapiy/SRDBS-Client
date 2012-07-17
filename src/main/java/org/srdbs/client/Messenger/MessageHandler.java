package org.srdbs.client.Messenger;

import org.srdbs.client.core.DbConnect;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/17/12
 * Time: 8:47 AM
 * For more details visit : http://www.thilina.org
 */
public class MessageHandler {

    public static String handleInit() {

        try {
            new DbConnect().initializeDB();
            return "Database initialized successfully.";
        } catch (Exception e) {

            return "Error in database initialization.";
        }
    }

    public static void handleData() {

    }

    public static void handleRequest() {

    }

    public static void handleDelete() {

    }

    public static void handleError() {

    }


}
