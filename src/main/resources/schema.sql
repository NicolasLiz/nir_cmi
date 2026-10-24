CREATE TABLE IF NOT EXISTS internacoes (
    cd_atendimento INTEGER PRIMARY KEY,
    cd_unid_int TEXT,
    ds_unid_int TEXT,
    cd_paciente INTEGER,
    nm_paciente TEXT,
    leito TEXT,
    prev_alta TEXT,        -- SQLite stores LocalDateTime as ISO-8601 text strings
    dias_internado INTEGER,
    alerta_dias TEXT
);

CREATE TABLE IF NOT EXISTS users (
    userID TEXT PRIMARY KEY,
    userName TEXT NOT NULL,
    userEmail TEXT UNIQUE NOT NULL,
    userPassword TEXT NOT NULL,
    userPermissions INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS units (
    name TEXT PRIMARY KEY,
    sectors TEXT, --comma separated list of sector names
);

CREATE TABLE IF NOT EXISTS sector (
    name TEXT PRIMARY KEY,
    rooms TEXT, --comma separated list of room numbers
);

CREATE TABLE IF NOT EXISTS room (
    number_ INTEGER PRIMARY KEY,
    beds TEXT, --comma separated list of bed numbers
);

CREATE TABLE IF NOT EXISTS bed (
    number_ INTEGER PRIMARY KEY,
    patient INTEGER,
    FOREIGN KEY (patient) REFERENCES patients(ID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS patients (
    ID INTEGER PRIMARY KEY,
    serviceId INTEGER,
    name TEXT,
    birthDate TEXT,
    sex TEXT,
    unit TEXT,
    FOREIGN KEY (unit) REFERENCES units(ID) 
        ON DELETE RESTRICT
        ON UPDATE CASCADE

    bed INTEGER,
    FOREIGN KEY (bed) REFERENCES beds(number_) 
        ON DELETE RESTRICT
        ON UPDATE CASCADE

    motherName TEXT,
    projRelease TEXT
);


CREATE TABLE IF NOT EXISTS transfers (
    ID INTEGER PRIMARY KEY,
    patient INTEGER,
    FOREIGN KEY (patient) REFERENCES patients(ID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE

    from_ INTEGER,
    FOREIGN KEY (from_) REFERENCES beds(number_)
        ON DELETE RESTRICT
        ON UPDATE CASCADE

    to_ INTEGER
    FOREIGN KEY (to_) REFERENCES patients(number_)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS releases (
    ID INTEGER PRIMARY KEY,
    patient INTEGER,
    FOREIGN KEY (patient) REFERENCES patients(ID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE

    date_ TEXT
);
