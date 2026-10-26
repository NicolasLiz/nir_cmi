package br.com.nircmi.model;

import java.time.LocalDateTime;

import br.com.nircmi.model.Admission;
import br.com.nircmi.model.Bed;
import br.com.nircmi.model.User;

public class Transfer {
    public Long transferID;
    public Long transferAdmissionID;
    public Long transferFromBedID;
    public Long transferToBedID;
    public LocalDateTime transferDate;
    public String transferReason;
    public Long transferUserID;

    public Admission transferAdmission;
    // bad names. not changing it
    public Bed transferFromBed;
    public Bed transferToBed;
    public User transferUser;
}
