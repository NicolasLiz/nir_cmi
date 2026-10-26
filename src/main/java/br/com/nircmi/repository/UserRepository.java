package br.com.nircmi.repository;

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
public class UserRepository {
    private static final String CREATE_NEW_USER = """
        INSERT INTO users (userID, userName, userEmail, userPassword, userPermissions, userActive, userRegisterDate) 
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    private static final String FIND_USER_BY_EMAIL = """
        SELECT userID, userName, userEmail, userPassword, userPermissions, userActive, userRegisterDate 
        FROM users WHERE userEmail = ?
    """;

    private static final String FIND_USER_BY_NAME = """
        SELECT userID, userName, userEmail, userPassword, userPermissions, userActive, userRegisterDate 
        FROM users WHERE userName = ?
    """;

    private static final String FIND_USERS_BY_PERM = """
        SELECT userID, userName, userEmail, userPassword, userPermissions, userActive, userRegisterDate 
        FROM users WHERE (userPermissions & ?) = ?
    """;

    private static final String UPDATE_USER = """
        UPDATE users 
        SET userName = ?, userEmail = ?, userPassword = ?, userPermissions = ?, userActive = ?
        WHERE userID = ?
    """;

    private static final String DELETE_USER_BY_EMAIL = """
        DELETE FROM users WHERE userEmail = ?
    """;

    private static final String DELETE_USER_BY_NAME = """
        DELETE FROM users WHERE userName = ?
    """;

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.userID = UUID.fromString(rs.getString("userID"));
        user.userName = rs.getString("userName");
        user.userEmail = rs.getString("userEmail");
        user.userPassword = rs.getString("userPassword");
        user.userPermissions = rs.getLong("userPermissions");
        user.userActive = rs.getInt("userActive");

        String str = rs.getString("userRegisterDate");
        if (str != null && !str.isEmpty()) {
            user.userRegisterDate = LocalDateTime.parse(str);
        } else {
            user.userRegisterDate = null;
        }

        return user;
    };

    // get
    public User getUserByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(FIND_USER_BY_EMAIL, userRowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User getUserByName(String name) {
        try {
            // Fixed: Now uses FIND_USER_BY_NAME query instead of FIND_USER_BY_EMAIL
            return jdbcTemplate.queryForObject(FIND_USER_BY_NAME, userRowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<User> getUsersByPermission(long permissions) {
        return jdbcTemplate.query(FIND_USERS_BY_PERM, userRowMapper, permissions, permissions);
    }

    // create
    public boolean createUser(User user) {
        // Prevent creation if email or username is already taken
        if (getUserByEmail(user.userEmail) != null || getUserByName(user.userName) != null) {
            return false;
        }

        // Set default fallback values if not provided
        if (user.userID == null) {
            user.userID = UUID.randomUUID();
        }
        if (user.userRegisterDate == null) {
            user.userRegisterDate = LocalDateTime.now();
        }

        int rowsAffected = jdbcTemplate.update(
            CREATE_NEW_USER,
            user.userID.toString(),
            user.userName,
            user.userEmail,
            user.userPassword,
            user.userPermissions,
            user.userActive,      // Defaulting to 1 (active) if not set: user.userActive != 0 ? user.userActive : 1
            user.userRegisterDate.toString()
        );

        return rowsAffected > 0;
    }

    // update
    public boolean updateUser(User user) {
        if (user.userID == null) {
            return false;
        }

        int rowsAffected = jdbcTemplate.update(
            UPDATE_USER,
            user.userName,
            user.userEmail,
            user.userPassword,
            user.userPermissions,
            user.userActive,
            user.userID.toString()
        );

        return rowsAffected > 0;
    }

    // delete
    public int deleteUser(User user) {
        if (user.userEmail != null && getUserByEmail(user.userEmail) != null) {
            return jdbcTemplate.update(DELETE_USER_BY_EMAIL, user.userEmail);
        }

        if (user.userName != null && getUserByName(user.userName) != null) {
            return jdbcTemplate.update(DELETE_USER_BY_NAME, user.userName);
        }

        return 0;
    }
}
