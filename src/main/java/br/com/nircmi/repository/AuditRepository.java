package br.com.nircmi.repository;

import br.com.nircmi.model.Audit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class AuditRepository {
    private static final String FIND_ALL_AUDITS = """
        SELECT
            auditID;
            auditUserID;
            auditDate;
            auditAction;
            auditTable;
            auditRegister;
            auditPreviousValue;
            auditNewValue;
        FROM audits
        ORDER BY auditID;
    """;

    private final JdbcTemplate jdbcTemplate;

    public AuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Audit> auditRowMapper = (rs, rowNum) -> {
        Audit i = new Audit();
        
        // Map columns to model properties
        i.auditID = rs.getLong("auditID");
        i.auditUserID = rs.getLong("auditUserID");

        // Safe parsing for auditInDate
        String date = rs.getString("auditDate");
        if (date != null && !date.isEmpty()) {
            i.auditDate = LocalDateTime.parse(date);
        } else {
            i.auditDate = null;
        }

        i.auditAction = rs.getString("auditAction");
        i.auditTable = rs.getString("auditTable");
        i.auditRegister = rs.getString("auditRegister");
        i.auditPreviousValue = rs.getString("auditPreviousValue");
        i.auditNewValue = rs.getString("auditNewValue");

        return i;
    };

    public List<Audit> findCmiAdmissions() {
        return jdbcTemplate.query(FIND_ALL_AUDITS, auditRowMapper);
    }
}
