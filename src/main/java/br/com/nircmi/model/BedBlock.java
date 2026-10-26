package br.com.nircmi.model;

import java.time.LocalDateTime;

import br.com.nircmi.model.User;

public class BedBlock {
    public Long blockID;
    public Long blockBedID;
    public String blockReason;
    public LocalDateTime blockStartDate;
    public LocalDateTime blockEndDate;
    public Long blockUserID;

    public User blockUser;
}
