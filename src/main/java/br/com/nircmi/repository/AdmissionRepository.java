package br.com.nircmi.repository;

import br.com.nircmi.model.Admission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class AdmissionRepository {
    private static final String FIND_ALL_ADMISSIONS = """
        SELECT
            admissionID,
            admissionPatientId,
            admissionInDate,
            admissionOutDate,
            admissionStatus,
            admissionObservation
        FROM admissions
        ORDER BY admissionID;
    """;

    private final JdbcTemplate jdbcTemplate;

    public AdmissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Admission> admissionRowMapper = (rs, rowNum) -> {
        Admission i = new Admission();
        
        // Map columns to model properties
        i.admissionID = rs.getLong("admissionID");
        i.admissionPatientId = rs.getLong("admissionPatientId");

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

        return i;
    };

    public List<Admission> findCmiAdmissions() {
        return jdbcTemplate.query(FIND_ALL_ADMISSIONS, admissionRowMapper);
    }
}
