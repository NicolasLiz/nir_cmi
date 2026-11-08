package br.com.nircmi.repository;

import br.com.nircmi.model.Bed;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class BedRepository {
    private static final String FIND_ALL_BEDS = """
        SELECT
            bedID,
            bedRoomID,
            bedNumber,
            bedType, 
            bedStatus,
            bedObservation,
            bedRegisterDate
        FROM beds
        ORDER BY bedID;
    """;

    private static final String FIND_BED_BY_ID = """
        SELECT
            bedID,
            bedRoomID,
            bedNumber,
            bedType, 
            bedStatus,
            bedObservation,
            bedRegisterDate
        FROM beds
        WHERE bedID = ?;
    """;

    private static final String CREATE_NEW_BED = """
        INSERT INTO beds (bedRoomID, bedNumber, bedType, bedStatus, bedObservation, bedRegisterDate) 
        VALUES (?, ?, ?, ?, ?, ?);
    """;

    private static final String UPDATE_BED = """
        UPDATE rooms
        SET bedRoomID = ?, bedNumber = ?, bedType = ?, bedStatus = ?, bedObservation = ?, bedRegisterDate = ?
        where roomSectorID = ?;
    """;

    private final JdbcTemplate jdbcTemplate;

    public BedRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Bed> bedRowMapper = (rs, rowNum) -> {
        Bed i = new Bed();
        
        // Map columns to model properties
        i.bedID = rs.getLong("bedID");
        i.bedRoomID = rs.getLong("bedRoomID");
        i.bedNumber = rs.getInt("bedNumber");
        i.bedType = rs.getInt("bedType");
        i.bedStatus = rs.getInt("bedStatus");
        i.bedObservation = rs.getString("bedObservation");
        i.bedRegisterDate = LocalDateTime.parse(rs.getString("bedRegisterDate"));

        return i;
    };

    public List<Bed> getAllBeds() {
        return jdbcTemplate.query(FIND_ALL_BEDS, bedRowMapper);
    }

    public Bed getBedByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_BED_BY_ID, bedRowMapper, id);
    }

    public boolean createBed(Bed bed) {
        int rowsAffected = jdbcTemplate.update(CREATE_NEW_BED,
            bed.bedRoomID,
            bed.bedNumber,
            bed.bedType,
            bed.bedStatus,
            bed.bedObservation,
            bed.bedRegisterDate
        );

        return rowsAffected > 0;
    }

    public boolean updateBed(Bed bed) {
        if (bed.bedID == null) {
            return false;
        }

        int rowsAffected = jdbcTemplate.update(UPDATE_BED, 
            bed.bedRoomID,
            bed.bedNumber,
            bed.bedType,
            bed.bedStatus,
            bed.bedObservation,
            bed.bedRegisterDate,
            bed.bedID
        );

        return rowsAffected > 0;
    }
}

