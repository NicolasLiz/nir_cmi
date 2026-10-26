package br.com.nircmi.repository;

import br.com.nircmi.model.Sector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * 
 */
@Repository
public class SectorRepository {
    private static final String FIND_ALL_SECTORS = """
        SELECT
            sectorID,
            sectorName,
            sectorDescription,
            sectorActive
        FROM sectors
        ORDER BY sectorID;
    """;


    private static final String FIND_SECTOR_BY_ID = """
        SELECT
            sectorID,
            sectorName,
            sectorDescription,
            sectorActive
        FROM sectors
        WHERE sectorID = ?;
    """;

    private static final String CREATE_NEW_SECTOR = """
        INSERT INTO sectors (sectorName, sectorDescription, sectorActive) 
        VALUES (?, ?, ?);
    """;

    private final JdbcTemplate jdbcTemplate;

    public SectorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Sector> sectorRowMapper = (rs, rowNum) -> {
        Sector i = new Sector();
        
        // Map columns to model properties
        i.sectorID = rs.getLong("sectorID");
        i.sectorName = rs.getString("sectoName");
        i.sectorDescription = rs.getString("sectorDescription");
        i.sectorActive = rs.getInt("sectorActive");

        return i;
    };

    public List<Sector> getAllSectors() {
        return jdbcTemplate.query(FIND_ALL_SECTORS, sectorRowMapper);
    }

    public Sector getSectorByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_SECTOR_BY_ID, sectorRowMapper, id);
    }

    public boolean createSector(Sector sector) {
        int rowsAffected = jdbcTemplate.update(CREATE_NEW_SECTOR, 
                sector.sectorName,
                sector.sectorDescription,
                sector.sectorActive
        );

        return rowsAffected > 0;
    }
}
