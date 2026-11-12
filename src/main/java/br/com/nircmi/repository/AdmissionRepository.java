package br.com.nircmi.repository;

import br.com.nircmi.model.Admission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 
 */
@Repository
public class AdmissionRepository {
    private static final String FIND_ALL_ADMISSIONS = """
        SELECT
            admissionID,
            admissionPatientID,
            admissionInDate,
            admissionOutDate,
            admissionStatus,
            admissionObservation,
            admissionUserID 
        FROM admissions
        WHERE admissionStatus > 0
        ORDER BY admissionID;
    """;

    private static final String FIND_ADMISSION_BY_ID = """
        SELECT
            admissionID,
            admissionPatientID,
            admissionInDate,
            admissionOutDate,
            admissionStatus,
            admissionObservation,
            admissionUserID
        FROM admissions
        WHERE admissionID = ?;
    """;

    private static final String CREATE_NEW_ADMISSION = """
        INSERT INTO admissions (admissionPatientID, admissionInDate, admissionOutDate, admissionStatus, admissionObservation, admissionUserID)
        VALUES (?, ?, ?, ?, ?, ?);
    """;

    private static final String UPDATE_ADMISSION = """
        UPDATE admissions
        SET admissionStatus = ?, admissionObservation = ?
        WHERE admissionID = ?;
    """;

    private final JdbcTemplate jdbcTemplate;

    public AdmissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Admission> admissionRowMapper = (rs, rowNum) -> {
        Admission i = new Admission();
        
        // Map columns to model properties
        i.admissionID = rs.getLong("admissionID");
        i.admissionPatientID = rs.getLong("admissionPatientID");

        // Safe parsing for admissionInDate
        String inDateStr = rs.getString("admissionInDate");
        if (inDateStr != null && !inDateStr.isEmpty()) {
            i.admissionInDate = LocalDateTime.parse(inDateStr);
        } else {
            i.admissionInDate = null;
        }

        // Safe parsing for admissionOutDate
        String outDateStr = rs.getString("admissionOutDate");
        if (outDateStr != null && !outDateStr.isEmpty()) {
            i.admissionOutDate = LocalDateTime.parse(outDateStr);
        } else {
            i.admissionOutDate = null;
        }

        i.admissionStatus = rs.getInt("admissionStatus");
        i.admissionObservation = rs.getString("admissionObservation");

        i.admissionUserID = UUID.fromString(rs.getString("admissionUserID"));

        return i;
    };

    public List<Admission> getAllAdmissions() {
        return jdbcTemplate.query(FIND_ALL_ADMISSIONS, admissionRowMapper);
    }

    public Admission getAdmissionByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_ADMISSION_BY_ID, admissionRowMapper, id);
    }

    public boolean createAdmission(Admission admission) {
        int affectedRows =  jdbcTemplate.update(CREATE_NEW_ADMISSION,
                admission.admissionPatientID,
                admission.admissionInDate,
                admission.admissionOutDate,
                admission.admissionStatus,
                admission.admissionObservation,
                admission.admissionUserID.toString()
        );

        return affectedRows > 0;
    }

    public boolean updateAdmission(Admission admission) {
        int affectedRows =  jdbcTemplate.update(UPDATE_ADMISSION,
                admission.admissionStatus,
                admission.admissionObservation,
                admission.admissionID
        );

        return affectedRows > 0;
    }
}
