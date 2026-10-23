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
