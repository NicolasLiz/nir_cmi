package br.com.nircmi.repository;

import br.com.nircmi.model.Discharge;
import br.com.nircmi.model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Equivalente a server/queries.js (getCmiInternacoes).
 */
@Repository
public class DischargeRepository {
    private static final String FIND_ALL_DISCHARGES = """
        SELECT dischargeID, dischargePatientID, dischargeUserID, dischargeDate FROM discharges
        ORDER BY dischargeID;
    """;

    private static final String FIND_DISCHARGE_BY_ID = """
        SELECT dischargeID, dischargePatientID, dischargeUserID, dischargeDate FROM discharges
        WHERE dischargeID = ?;
    """;

    private static final String CREATE_NEW_DISCHARGE = """
        INSERT INTO discharges (dischargePatientID, dischargeUserID, dischargeDate) 
        VALUES (?, ?, ?);
        """;

    private final JdbcTemplate jdbcTemplate;

    public DischargeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Discharge> dischargeRowMapper = (rs, rowNum) -> {
        Discharge discharge = new Discharge();

        discharge.dischargeID = rs.getLong("dischargeID");
        discharge.dischargePatientID = rs.getLong("dischargePatientID");
        discharge.dischargeUserID = UUID.fromString(rs.getString("dischargeUserID"));
        discharge.dischargeDate = LocalDateTime.parse(rs.getString("dischargeDate"));

        return discharge;
    };

    // get
    public List<Discharge> getAllDischarges() {
        return jdbcTemplate.query(FIND_ALL_DISCHARGES, dischargeRowMapper);
    }

    public Discharge getDischargeByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_DISCHARGE_BY_ID, dischargeRowMapper, id);
    }

    public boolean createDischarge(Discharge discharge) {
        int rowsAffected = jdbcTemplate.update(CREATE_NEW_DISCHARGE,
            discharge.dischargePatientID,
            discharge.dischargeUserID.toString(),
            discharge.dischargeDate.toString()
        );

        return rowsAffected > 0;
    }
}
