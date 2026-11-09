package br.com.nircmi.repository;

import br.com.nircmi.model.BedStatusChange;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class BedStatusChangeRepository {
    private static final String FIND_ALL_STATUS_CHANGES = """
        SELECT
            statusChangeID,
            statusChangeBedID,
            statusChangeOld,
            statusChangeNew,
            statusChangeDate
        FROM bedStatusChanges
        ORDER BY bedID;
    """;

    private static final String FIND_STATUS_CHANGE_BY_ID = """
        SELECT
            statusChangeID,
            statusChangeBedID,
            statusChangeOld,
            statusChangeNew,
            statusChangeDate
        FROM bedStatusChanges
        WHERE statusChangeID = ?;
    """;

    private static final String CREATE_NEW_STATUS_CHANGE = """
        INSERT INTO bedStatusChanges (statusChangeBedID, statusChangeOld, statusChangeNew, statusChangeDate) 
        VALUES (?, ?, ?, ?);
    """;

    private static final String FIND_STATUS_CHANGES_BY_BED_ID = """
        SELECT
            statusChangeID,
            statusChangeBedID,
            statusChangeOld,
            statusChangeNew,
            statusChangeDate
        FROM bedStatusChanges
        WHERE statusChangeBedID = ?
        ORDER BY statusChangeID;
    """;


    private final JdbcTemplate jdbcTemplate;

    public BedStatusChangeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BedStatusChange> statusChangeRowMapper = (rs, rowNum) -> {
        BedStatusChange i = new BedStatusChange();
        
        // Map columns to model properties
        i.statusChangeID = rs.getLong("statusChangeID");
        i.statusChangeBedID = rs.getLong("statusChangeBedID");
        i.statusChangeOld = rs.getInt("statusChangeOld");
        i.statusChangeNew = rs.getInt("statusChangeNew");
        i.statusChangeDate = LocalDateTime.parse(rs.getString("statusChangeDate"));

        return i;
    };

    public List<BedStatusChange> getAllBedStatusChanges() {
        return jdbcTemplate.query(FIND_ALL_STATUS_CHANGES, statusChangeRowMapper);
    }

    public BedStatusChange getBedStatusChangeByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_STATUS_CHANGE_BY_ID, statusChangeRowMapper, id);
    }

    public boolean createBedStatusChange(BedStatusChange statusChange) {
        int rowsAffected = jdbcTemplate.update(CREATE_NEW_STATUS_CHANGE,
            statusChange.statusChangeBedID,
            statusChange.statusChangeOld,
            statusChange.statusChangeNew,
            statusChange.statusChangeDate
        );

        return rowsAffected > 0;
    }

    public BedStatusChange getBedStatusChangeByBedID(Long id) {
        return jdbcTemplate.queryForObject(FIND_STATUS_CHANGES_BY_BED_ID, statusChangeRowMapper, id);
    }

}

