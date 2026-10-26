package br.com.nircmi.model;

import java.time.LocalDateTime;
import br.com.nircmi.model.Patient;

/**
 * DTO equivalente as linhas retornadas por getCmiInternacoes() em server/queries.js
 */
public class Admission {
    public static final int aStatusActive = 1;
    public static final int aStatusInactive = 0;

    public Long admissionID;
    public Long admissionPatientID;
    public LocalDateTime admissionInDate;
    public LocalDateTime admissionOutDate;
    public int admissionStatus; //?
    public String admissionObservation;

    public Patient admissionPatient;
}
