Secure and Redundant Data Backup System - Client Software

Description

	SRDBS is a enterprise class data backup system. System can perform a fully automatic backup to several disaster 
	recovery (DR) sites. This system requires minimum of there clouds to do its backup process.

Installation

	Get source code (Optional if you have the binary distribution. Skip this step.)

		git clone git@github.com:thilinapiy/SRDBS-Client.git

		or

		Download a zip file of the code and unzip it.

		Then go to the root directory of the project and type

		in windows
			> mvn clean package

		in Linux
			$ mvn clean package

    Install binary distribution

        Unzip/untar the compressed file in to a secure location. Then set the path
        variable as

        in Windows
            > set SRDBSCLIENT_HOME=<path to the installation>
                 eg: SRDBSCLIENT_HOME=C:\Users\Thilina\Desktop\SRDBS-Client-${project.version}
            > set path=%SRDBSCLIENT_HOME%\bin;%PATH%

        in Linux
            $ export SRDBSCLIENT_HOME=<path to the installation>
                eg: export SRDBSCLIENT_HOME=/home/thilina/SRDBS-Client-${project.version}
            $ export PATH=$SRDBSCLIENT_HOME/bin:$PATH

    Create MySQL database

            > Create a separate database for the system on MySQL database.
                - CREATE DATABASE srdbsclientdb;

            > Create a user to the system.
                - CREATE USER srdbsclient;

            > Grant privileges.
                - GRANT ALL PRIVILEGES ON srdbsclientdb.* TO 'srdbsclient'@'127.0.0.1' IDENTIFIED BY 'password';


    Set configurations

        Update the "config/configure.conf" file.

        MySQL settings
        mysql.dbIPAddress=127.0.0.1   [Give the mysql database IP address on the cloud]
        mysql.dbName=srdbsclientdb    [Database name]
        mysql.dbPort=3306             [Database port address]
        mysql.dbUserName=srdbsclient  [Database user name]
        mysql.dbPassword=password     [Database users Password]

        Server settings
        server.cloudid=1                             [ID of the cloud {1,2,3}]
        server.ftplocation=/home/chathuranga         [Root path of the FTP user connecting from the main server]
        server.domainname=cloud1.serviceprovider.com [Domain name of the Cloud service accessible via the internet]
        server.port=55555                            [Message service Port]

    Run the server

        > srdbsstart.bat

        or

        $ srdbsstart

Dependencies
	Oracle Java JDK 7
		(http://www.oracle.com/technetwork/java/javase/downloads/java-se-jdk-7-download-432154.html)
	Maven 3.0
		(http://maven.apache.org/download.html)
	MySQL database
	    (http://dev.mysql.com/downloads/)
Developers

	Thilina Piyasundara
	Chanaka Yapa
	Isanka Gayan
	Prabodha Amarasinghe