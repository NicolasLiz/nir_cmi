package br.com.nircmi.model;

import br.com.nircmi.model.Patient;

import java.time.LocalDateTime;

public class Unit {
    public String unitName;

    public Sector[] unitSectors;
};

class Sector {
    public String sectorName;

    public Room[] sectorRooms;
};

class Room {
    public static enum rStatus {
        available,
        busy,
        reserved,
        sanitation,
        maintenance,
        blocked
    };

    public static int rTypeTmp = 1 << 1;

    public int id;
    public int roomNumber;
    public Sector at;
    public int type;
    public int status;
    public String[] observations;
    public LocalDateTime registeredAt;
    public int workingOrder;

    public Bed[] roomBeds;
};

class Bed {
    public int bedNumber;
    public Patient patient;
};

// unidades
// setores
// quartos
// leitos
//
// pacientes
// internaçoes
// transferencias
// altas
// relatorios
