package br.com.nircmi.model;

import br.com.nircmi.model.Bed;
import br.com.nircmi.model.Sector;

public class Room {
    public Long roomID;
    public Long roomSectorID;
    public String roomIdentification;
    public String roomType;
    public int roomActive;

    public Sector roomSector;
    public Bed[] roomBeds;
};

