package br.com.nircmi.repository;

import br.com.nircmi.model.Patient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class PatientRepository {
    private static final String FIND_ALL_PATIENTS = """
        SELECT
            patientID,
            patientName,
            PatientBirthDate,
            patientDocument,
            patientChart,
            patientRegisterDate
        FROM patients
        ORDER BY patientID;
    """;

    private static final String FIND_PATIENT_BY_ID = """
        SELECT
            patientID,
            patientName,
            PatientBirthDate,
            patientDocument,
            patientChart,
            patientRegisterDate
        FROM patients
        WHERE patientID = ?;
    """;

    private static final String CREATE_NEW_PATIENT = """
        INSERT INTO patients (patientName, patientBirthDate, patientDocument, patientChart, patientRegisterDate)
        VALUES (?, ?, ?, ?, ?);
    """;

    private static final String UPDATE_PATIENT = """
        UPDATE patients 
        SET patientName = ?, patientBirthDate = ?, patientDocument = ?, patientChart = ?, patientRegisterDate = ?
        WHERE patientID = ?;
    """;

    private final JdbcTemplate jdbcTemplate;

    public PatientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Patient> patientRowMapper = (rs, rowNum) -> {
        Patient i = new Patient();
        
        // Map columns to model properties
        i.patientID = rs.getLong("patientID");
        i.patientName = rs.getString("patientName");

        // Safe parsing for patientInDate
        String bd = rs.getString("patientBirthDate");
        if (bd != null && !bd.isEmpty()) {
            i.patientBirthDate = LocalDateTime.parse(bd);
        } else {
            i.patientBirthDate = null;
        }

        i.patientDocument = rs.getString("patientDocument");
        i.patientChart = rs.getString("patientChart");

        // Safe parsing for patientInDate
        String rd = rs.getString("patientRegisterDate");
        if (rd != null && !rd.isEmpty()) {
            i.patientRegisterDate = LocalDateTime.parse(rd);
        } else {
            i.patientRegisterDate = null;
        }

        return i;
    };

    public List<Patient> getAllPatients() {
        return jdbcTemplate.query(FIND_ALL_PATIENTS, patientRowMapper);
    }

    public Patient getPatientByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_PATIENT_BY_ID, patientRowMapper, id);
    }

    public boolean createPatient(Patient patient) {
        int rows_affected = jdbcTemplate.update(CREATE_NEW_PATIENT,
            patient.patientName,
            patient.patientBirthDate,
            patient.patientDocument,
            patient.patientChart,
            patient.patientRegisterDate
        );

        return rows_affected > 0;
    }

    public boolean updatePatient(Patient patient) {
        int rows_affected = jdbcTemplate.update(UPDATE_PATIENT,
            patient.patientName,
            patient.patientBirthDate,
            patient.patientDocument,
            patient.patientChart,
            patient.patientRegisterDate,
            patient.patientID
        );

        return rows_affected > 0;
    }
}
