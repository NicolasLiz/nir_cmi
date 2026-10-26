package br.com.nircmi.repository;

import br.com.nircmi.model.Room;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class RoomRepository {
    private static final String FIND_ALL_ROOMS = """
        SELECT
            roomID,
            roomSectorID,
            roomIdentification,
            roomType,
            roomActive,
        FROM rooms
        ORDER BY roomID;
    """;

    private static final String FIND_ROOM_BY_ID = """
        SELECT
            roomID,
            roomSectorID,
            roomIdentification,
            roomType,
            roomActive,
        FROM rooms
        WHERE roomID = ?;
    """;

    private final JdbcTemplate jdbcTemplate;

    public RoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Room> roomRowMapper = (rs, rowNum) -> {
        Room i = new Room();
        
        // Map columns to model properties
        i.roomID = rs.getLong("roomID");
        i.roomSectorID = rs.getLong("roomSectorID");
        i.roomIdentification = rs.getString("roomIdentification");
        i.roomType = rs.getInt("roomType");
        i.roomActive = rs.getInt("roomActive");

        return i;
    };

    public List<Room> getAllRooms() {
        return jdbcTemplate.query(FIND_ALL_ROOMS, roomRowMapper);
    }

    public Room getRoomByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_ROOM_BY_ID, roomRowMapper, id);
    }
}
