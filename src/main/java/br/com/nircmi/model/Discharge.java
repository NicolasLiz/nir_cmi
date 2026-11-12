package br.com.nircmi.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Discharge {
    public Long dischargeID;
    public Long dischargePatientID;
    public UUID dischargeUserID;
    public LocalDateTime dischargeDate;
}
