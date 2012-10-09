# Secure and Redundant Data Backup System - Client Software

## Description

    SRDBS is a enterprise class data backup system. System can perform a fully automatic backup to several disaster 
    recovery (DR) sites. This system requires minimum of there clouds to do its backup process.

## Installation

### Get source code (Optional if you have the binary distribution. Skip this step.)

		git clone git@github.com:thilinapiy/SRDBS-Client.git
		or
		Download a zip file of the code and unzip it.

		Then go to the root directory of the project and type

		in windows
			> mvn clean package

		in Linux
			$ mvn clean package

### Install binary distribution

        Unzip/untar the compressed file in to a secure location. Then set the path
        variable as

        in Windows
            > set SRDBSCLIENT_HOME=<path to the installation>
                 eg: SRDBSCLIENT_HOME=C:\Users\Thilina\Desktop\SRDBS-Client-<version>
            > set path=%SRDBSCLIENT_HOME%\bin;%PATH%

        in Linux
            $ export SRDBSCLIENT_HOME=<path to the installation>
                eg: export SRDBSCLIENT_HOME=/home/thilina/SRDBS-Client-<version>
            $ export PATH=$SRDBSCLIENT_HOME/bin:$PATH

### Create MySQL database

            > Create a separate database for the system on MySQL database.
                - CREATE DATABASE srdbsclientdb;

            > Create a user to the system.
                - CREATE USER srdbsclient;

            > Grant privileges.
                - GRANT ALL PRIVILEGES ON srdbsclientdb.* TO 'srdbsclient'@'127.0.0.1' IDENTIFIED BY 'password';

### Run the server

        > srdbsstart.bat
        or
        $ srdbsstart

### To see the UI visit:

        https://localhost:8080/setup

## Dependencies

- [__Oracle__ Java JDK 7] (http://www.oracle.com/technetwork/java/javase/downloads/java-se-jdk-7-download-432154.html)

- [Maven __3.0__] (http://maven.apache.org/download.html)

- [MySQL database] (http://dev.mysql.com/downloads/)

## Developers

- [Thilina Piyasundara] (http://thilina.org)

- Chanaka Yapa

- Isanka Gayan

- Prabodha Amarasinghe
