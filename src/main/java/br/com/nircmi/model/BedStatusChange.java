package br.com.nircmi.model;

import br.com.nircmi.model.Bed;

import java.time.LocalDateTime;

public class BedStatusChange {
    public static final int bStatAvailable = 1 << 0;
    public static final int bStatBusy = 1 << 1;
    public static final int bStatReserved = 1 << 2;
    public static final int bStatHigienization = 1 << 3;
    public static final int bStatMaintenance = 1 << 4;
    public static final int bStatBlocked = 1 << 5;

    public Long statusChangeID;
    public Long statusChangeBedID;
    public int statusChangeOld;
    public int statusChangeNew;
    public LocalDateTime statusChangeDate;

    public Bed bed;
};

