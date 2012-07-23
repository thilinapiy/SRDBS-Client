CREATE DATABASE srdbsclientdb;

CREATE USER srdbsclient;

GRANT ALL PRIVILEGES ON srdbsclientdb.* TO 'srdbsclient'@'127.0.0.1' IDENTIFIED BY 'password';

USE srdbsclientdb;

DROP TABLE sp_File;
DROP TABLE full_File;

DROP TABLE sp_File;
DROP TABLE full_File;

CREATE TABLE full_file(
	F_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	FName VARCHAR(100),
	FSize BIGINT,
	HashValue VARCHAR(100),
	Up_Date VARCHAR(100)
);


Create Table Sp_File(
    SP_FILE_ID int not null auto_increment,
    F_ID int,
    SP_FileName varchar(400),
    F_SIZE Bigint,
    HashValue varchar(200),
    Ref_Cloud_ID int,
    Raid_Ref int,
    Remote_path varchar(400),

    Constraint Pk_SP_FileID_1 Primary key(SP_FILE_ID),
    Constraint FK_SP_FileID_2 Foreign key (F_ID) References Full_File (F_ID)
);

SELECT  * from Sp_File; SELECT  * from full_file;
