package br.com.nircmi.model;

import br.com.nircmi.model.Unit;
import java.time.LocalDateTime;

public class Patient {
    public String name;
    public int ID;
    public int serviceId;
    public LocalDateTime birthDate;
    public String sex;
    public Unit unit;
    public Bed bed;
    public String motherName;
    public LocalDateTime projRelease;
};
