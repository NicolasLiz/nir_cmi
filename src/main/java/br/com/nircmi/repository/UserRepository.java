package br.com.nircmi.repository;

import br.com.nircmi.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        INSERT INTO users (userID, userName, userEmail, userPassword, userPersmissions) VALUES (?, ?, ?, ?, ?)
    """;

    private static final String FIND_USER_EMAIL = """
        SELECT userID, userName, userEmail, userPassword, userPersmissions FROM users WHERE userEmail = ?
    """;

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // get
    public User getUserByEmail(String email) {
        try {
            return jbdcTemplate.queryForObject(sql, (rs, rowNum) -> {
                User user = new User();
                user.userID = rs.getString("userID");
                user.userName = rs.getString("userName");
                user.userEmail = rs.getString("userEmail");
                user.userPassword = rs.getString("userPassword");
                user.userPermissions = rs.getString("userPermissions");
                return user;
            }, email)
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // create
    public boolean createUser(User user) {
        if (getUserByEmail(user.userEmail) == null) {
            jbdcTemplate.update(
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
    // update
}
