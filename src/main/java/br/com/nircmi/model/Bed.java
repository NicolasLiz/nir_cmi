package br.com.nircmi.model;

import br.com.nircmi.model.Room;

import java.time.LocalDateTime;

public class Bed {
    public static final int bStatAvailable = 1 << 0;
    public static final int bStatBusy = 1 << 1;
    public static final int bStatReserved = 1 << 2;
    public static final int bStatHigienization = 1 << 3;
    public static final int bStatMaintenance = 1 << 4;
    public static final int bStatBlocked = 1 << 5;

    public Long bedID;
    public Long bedRoomID;
    public int bedNumber;
    public int bedType; 
    public int bedStatus;
    public String bedObservation;
    public LocalDateTime bedRegisterDate;
    public int bedActive;

    public Room bedRoom;
};

