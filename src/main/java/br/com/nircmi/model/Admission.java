package br.com.nircmi.model;

import java.time.LocalDateTime;
import br.com.nircmi.model.Patient;

import java.util.UUID;

/**
 * DTO equivalente as linhas retornadas por getCmiInternacoes() em server/queries.js
 */
public class Admission {
    public static final int aStatusInactive = 0;
    public static final int aStatusOpen = 1;
    public static final int aStatusClosed = 2;

    public Long admissionID;
    public Long admissionPatientID;
    public LocalDateTime admissionInDate;
    public LocalDateTime admissionOutDate;
    public int admissionStatus;
    public String admissionObservation;
    public UUID admissionUserID;

    public Patient admissionPatient;
}
