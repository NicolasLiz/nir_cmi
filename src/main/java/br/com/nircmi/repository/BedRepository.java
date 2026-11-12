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
            bedRegisterDate,
            bedActive
        FROM beds
        WHERE bedActive = 1
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
            bedRegisterDate,
            bedActive
        FROM beds
        WHERE bedID = ?;
    """;

    private static final String CREATE_NEW_BED = """
        INSERT INTO beds (bedRoomID, bedNumber, bedType, bedStatus, bedObservation, bedRegisterDate, bedActive) 
        VALUES (?, ?, ?, ?, ?, ?, ?);
    """;

    private static final String UPDATE_BED = """
        UPDATE beds
        SET bedRoomID = ?, bedNumber = ?, bedType = ?, bedStatus = ?, bedObservation = ?, bedRegisterDate = ?, bedActive = ?
        WHERE bedID = ?;
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
        i.bedActive = rs.getInt("bedActive");

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
            bed.bedRegisterDate,
            bed.bedActive
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
            bed.bedActive,
            bed.bedID
        );

        return rowsAffected > 0;
    }
}

