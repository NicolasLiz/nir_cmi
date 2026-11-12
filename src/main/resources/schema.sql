CREATE TABLE IF NOT EXISTS users (
    userID TEXT PRIMARY KEY NOT NULL, --uuid
    userName TEXT NOT NULL,
    userEmail TEXT UNIQUE NOT NULL,
    userPassword TEXT NOT NULL,
    userPermissions INTEGER NOT NULL,
    userActive INTEGER NOT NULL,
    userRegisterDate TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS sectors (
    sectorID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, --sequential
    sectorName TEXT NOT NULL,
    sectorDescription TEXT,
    sectorActive INTEGER NOT NULL 
);

CREATE TABLE IF NOT EXISTS rooms (
    roomID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    roomSectorID INTEGER NOT NULL,
    roomIdentification TEXT, --???
    roomType TEXT NOT NULL, --flags
    roomActive INTEGER NOT NULL,

    FOREIGN KEY (roomSectorID) REFERENCES sectors(sectorID)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS beds (
    bedID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    bedRoomID INTEGER NOT NULL,
    bedNumber INTEGER NOT NULL,
    bedType INTEGER NOT NULL,
    bedStatus INTEGER NOT NULL,
    bedObservation TEXT,
    bedRegisterDate TEXT NOT NULL,
    bedActive INTEGET NOT NULL,

    FOREIGN KEY (bedRoomID) REFERENCES rooms(roomID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS patients (
    patientID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    patientName TEXT NOT NULL,
    patientBirthDate TEXT NOT NULL,
    patientDocument TEXT,
    patientChart TEXT,
    patientRegisterDate TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS bedStatusChanges (
    statusChangeID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    statusChangeBedID INTEGER NOT NULL,
    statusChangeOld INTEGER NOT NULL,
    statusChangeNew INTEGER NOT NULL,
    statusChangeDate TEXT,
    
    FOREIGN KEY (statusChangeBedID) REFERENCES beds(bedID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS admissions (
    admissionID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    admissionPatientID INTEGER NOT NULL,
    admissionInDate TEXT,
    admissionOutDate TEXT,
    admissionStatus INTEGER, --flags
    admissionObservation TEXT,
    admissionUserID TEXT NOT NULL, -- usuario responsavel pela internação

    FOREIGN KEY (admissionPatientID) REFERENCES patients(patientID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    FOREIGN KEY (admissionUserID) REFERENCES users(userID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS transfers (
    transferID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    transferAdmissionID INTEGER,
    transferFromBedID INTEGER,
    transferToBedID INTEGER,
    transferDate TEXT,
    transferReason TEXT,
    transferUserID TEXT NOT NULL, -- uuid

    FOREIGN KEY (transferAdmissionID) REFERENCES admissions(admissionID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    FOREIGN KEY (transferFromBedID) REFERENCES beds(bedID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    FOREIGN KEY (transferToBedID) REFERENCES beds(bedID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    FOREIGN KEY (transferUserID) REFERENCES users(userID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS discharges (
    dischargeID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    dischargePatientID INTEGER,
    dischargeUserID TEXT NOT NULL,
    dischargeDate TEXT NOT NULL,

    FOREIGN KEY (dischargePatientID) REFERENCES patients(patientID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    FOREIGN KEY (dischargeUserID) REFERENCES users(userID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXITS audits (
    auditID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    auditUserID TEXT,
    auditDate TEXT,
    auditAction TEXT,
    auditTable TEXT,
    auditRegister TEXT,
    auditPreviousValue TEXT,
    auditNewValue TEXT,

    FOREIGN KEY (auditUserID) REFERENCES users(userID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);
