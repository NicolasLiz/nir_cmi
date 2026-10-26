package br.com.nircmi.model;

import java.time.LocalDateTime;

public class BedOccupation {
    public Long occupationID;
    public Long occupationAdmissionID;
    public Long occupationBedID;
    public LocalDateTime occupationInDate;
    public LocalDateTime occupationOutDate;
    public int occupationActive;
}
