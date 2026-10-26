package br.com.nircmi.model;

import java.time.LocalDateTime;

public class Audit {
    public Long auditID;
    public Long auditUserID;
    public LocalDateTime auditDate;
    public String auditAction;
    public String auditTable;
    public String auditRegister;
    public String auditPreviousValue;
    public String auditNewValue;
}
