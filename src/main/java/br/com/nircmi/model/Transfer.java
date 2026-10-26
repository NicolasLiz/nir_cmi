package br.com.nircmi.model;

import br.com.nircmi.model.Patient;
import br.com.nircmi.model.Unit;

public class Transfer {
    public Long transferID;
    public Long transferAdmissionID;
    public Long transferFromBed;
    public Long transferToBed;
    public LocalDateTime transferDate;
    public String transferReason;
    public Long transferUserID;
}
