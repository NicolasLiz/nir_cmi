package br.com.nircmi.repository;

import br.com.nircmi.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Equivalente a server/queries.js (getCmiInternacoes).
 */
@Repository
public class UserRepository {
    private static final String GET_ALL_USERS = """
        SELECT
            ds_unid_int      AS dsUnidInt,
            cd_unid_int      AS cdUnidInt,
            cd_atendimento   AS cdAtendimento,
            cd_paciente      AS cdPaciente,
            nm_paciente      AS nmPaciente,
            leito            AS leito,
            prev_alta        AS prevAlta,
            dias_internado   AS diasInternado,
            alerta_dias      AS alertaDias
        FROM internacoes
        ORDER BY cd_atendimento;
    """;

    private static final String CREATE_NEW_USER = """
        INSERT INTO users (userID, userName, userEmail, userPassword, userPermissions) VALUES (?, ?, ?, ?, ?)
    """;

    private static final String FIND_USER_EMAIL = """
        SELECT userID, userName, userEmail, userPassword, userPermissions FROM users WHERE userEmail = ?
    """;

    private static final String FIND_USER_NAME = """
        SELECT userID, userName, userEmail, userPassword, userPermissions FROM users WHERE userName = ?
    """;

    private static final String FIND_USER_PERM = """
        SELECT userID, userName, userEmail, userPassword, userPermissions FROM users WHERE (userPermissions & ?) = ?
    """;

    private static final String DELETE_USER_EMAIL = """
        DELETE FROM users WHERE userEmail = ?
    """;

    private static final String DELETE_USER_NAME = """
        DELETE FROM users WHERE userName = ?
    """;

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // get
    public User getUserByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(FIND_USER_EMAIL, (rs, rowNum) -> {
                User user = new User();
                user.userID = UUID.fromString(rs.getString("userID"));
                user.userName = rs.getString("userName");
                user.userEmail = rs.getString("userEmail");
                user.userPassword = rs.getString("userPassword");
                user.userPermissions = rs.getLong("userPermissions");
                return user;
            }, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User getUserByName(String name) {
        try {
            return jdbcTemplate.queryForObject(FIND_USER_EMAIL, (rs, rowNum) -> {
                User user = new User();
                user.userID = UUID.fromString(rs.getString("userID"));
                user.userName = rs.getString("userName");
                user.userEmail = rs.getString("userEmail");
                user.userPassword = rs.getString("userPassword");
                user.userPermissions = rs.getLong("userPermissions");
                return user;
            }, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<User> getUsersByPermission(long permissions) {
        return jdbcTemplate.query(FIND_USER_PERM, (rs, rowNum) -> {
            User user = new User();
            user.userID = UUID.fromString(rs.getString("userID"));
            user.userName = rs.getString("userName");
            user.userEmail = rs.getString("userEmail");
            user.userPassword = rs.getString("userPassword");
            user.userPermissions = rs.getLong("userPermissions");
            return user;
        }, permissions, permissions);
    }

    // create
    public boolean createUser(User user) {
        // only create user if there is no duplicate name and email
        if ((getUserByEmail(user.userEmail) == null) && (getUserByName(user.userName) == null)) {
            jdbcTemplate.update(
                CREATE_NEW_USER,
                user.userID,
                user.userName,
                user.userEmail,
                user.userPassword,
                user.userPermissions
            );

            return true;
        } else {
            return false;
        }
    }

    // delete
    // fill userName and/or userEmail. returns 0 if it didnt find the user
    public int deleteUser(User user) {
        if (getUserByEmail(user.userEmail) != null) {
            return jdbcTemplate.update(
                DELETE_USER_EMAIL,
                user.userEmail
            );
        }

        if (getUserByName(user.userName) != null) {
            return jdbcTemplate.update(
                DELETE_USER_NAME,
                user.userName
            );
        }

        return 0;
    }
   
    // update
}
