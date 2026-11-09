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
            roomActive
        FROM rooms
        WHERE roomActive = 1
        ORDER BY roomID;
    """;

    private static final String FIND_ROOM_BY_ID = """
        SELECT
            roomID,
            roomSectorID,
            roomIdentification,
            roomType,
            roomActive
        FROM rooms
        WHERE roomID = ?;
    """;

    private static final String CREATE_NEW_ROOM = """
        INSERT INTO rooms (roomSectorID, roomIdentification, roomType, roomActive) 
        VALUES (?, ?, ?, ?);
    """;

    private static final String UPDATE_ROOM = """
        UPDATE rooms
        SET roomSectorID = ?, roomIdentification = ?, roomType = ?, roomActive = ?
        where roomSectorID = ?;
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
        i.roomType = rs.getString("roomType");
        i.roomActive = rs.getInt("roomActive");

        return i;
    };

    public List<Room> getAllRooms() {
        return jdbcTemplate.query(FIND_ALL_ROOMS, roomRowMapper);
    }

    public Room getRoomByID(Long id) {
        return jdbcTemplate.queryForObject(FIND_ROOM_BY_ID, roomRowMapper, id);
    }

    public boolean createRoom(Room room) {
        int rowsAffected = jdbcTemplate.update(CREATE_NEW_ROOM,
            room.roomSectorID,
            room.roomIdentification,
            room.roomType,
            room.roomActive
        );

        return rowsAffected > 0;
    }

    public boolean updateRoom(Room room) {
        if (room.roomID == null) {
            return false;
        }

        int rowsAffected = jdbcTemplate.update(UPDATE_ROOM, 
            room.roomSectorID,
            room.roomIdentification,
            room.roomType,
            room.roomActive,
            room.roomID
        );

        return rowsAffected > 0;
    }
}
