package br.com.nircmi.model;

import java.time.LocalDateTime;

import br.com.nircmi.model.Admission;
import br.com.nircmi.model.Bed;

public class BedOccupation {
    public Long occupationID;
    public Long occupationAdmissionID;
    public Long occupationBedID;
    public LocalDateTime occupationInDate;
    public LocalDateTime occupationOutDate;
    public int occupationActive;

    public Admission occupationAdmission;
    public Bed occupationBed;
}
