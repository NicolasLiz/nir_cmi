package br.com.nircmi.model;

import java.time.LocalDateTime;

import br.com.nircmi.model.User;

public class Audit {
    public Long auditID;
    public Long auditUserID;
    public LocalDateTime auditDate;
    public String auditAction;
    public String auditTable;
    public String auditRegister;
    public String auditPreviousValue;
    public String auditNewValue;

    public User auditUser;
}

