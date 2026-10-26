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
            bedInDate,
            bedOutDate,
            bedStatus,
            bedObservation
        FROM beds
        ORDER BY bedID;
    """;

    private static final String FIND_ALL_BEDS = """
        SELECT
            bedID,
            bedRoomID,
            bedInDate,
            bedOutDate,
            bedStatus,
            bedObservation
        FROM beds
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
        i.bedIdentification = rs.getString("bedIdentification");
        i.bedType = rs.getInt("bedType");
        i.bedActive = rs.getInt("bedActive");

        return i;
    };

    public List<Bed> getAllBeds() {
        return jdbcTemplate.query(FIND_ALL_BEDS, bedRowMapper);
    }

    public Bed getBedById(Long id) {
        return jdbcTemplate.query(FIND_BED_BY_ID id);
    }
}
