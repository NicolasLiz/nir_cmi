package br.com.nircmi.repository;

import br.com.nircmi.model.BedOccupation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class BedOccupationRepository {
    private static final String FIND_ALL_OCCUPATIONS = """
        SELECT
            occupationID;
            occupationAdmissionID;
            occupationBedID;
            occupationInDate;
            occupationOutDate;
            occupationActive;
        FROM occupations
        ORDER BY occupationID;
    """;

    private final JdbcTemplate jdbcTemplate;

    public BedOccupationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BedOccupation> occupationRowMapper = (rs, rowNum) -> {
        BedOccupation i = new BedOccupation();
        
        // Map columns to model properties
        i.occupationID = rs.getLong("occupationID");
        i.occupationAdmissionID = rs.getLong("occupationAdmissionID");
        i.occupationBedID = rs.getLong("occupationBedID");

        // Safe parsing for occupationInDate
        String iDate = rs.getString("occupationInDate");
        if (iDate != null && !iDate.isEmpty()) {
            i.occupationInDate = LocalDateTime.parse(iDate);
        } else {
            i.occupationInDate = null;
        }

        String oDate = rs.getString("occupationOutDate");
        if (oDate != null && !oDate.isEmpty()) {
            i.occupationOutDate = LocalDateTime.parse(oDate);
        } else {
            i.occupationOutDate = null;
        }

        i.occupationActive = rs.getString("occupationActive");

        return i;
    };

    public List<BedOccupation> findCmiOccupations() {
        return jdbcTemplate.query(FIND_ALL_OCCUPATIONS, occupationRowMapper);
    }
}
