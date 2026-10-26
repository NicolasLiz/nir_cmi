package br.com.nircmi.model;

import br.com.nircmi.model.Room;

public class Sector {
    public Long sectorID;
    public String sectorName;
    public String sectorDescription;
    public int sectorActive;

    public Room[] sectorRooms; // not inside the database but filled when querying
};
