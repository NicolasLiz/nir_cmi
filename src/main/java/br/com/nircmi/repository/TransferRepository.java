package br.com.nircmi.repository;

import br.com.nircmi.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 
 */
@Repository
public class TransferRepository {
    private static final String FIND_ALL_TRANSFERS = """
        SELECT
            transferID;
            transferAdmissionID;
            transferFromBedID;
            transferToBedID;
            transferDate;
            transferReason;
            transferUserID;
        FROM transfers
        ORDER BY transferID;
    """;

    private static final String FIND_ALL_TRANSFERS = """
        SELECT
            transferID;
            transferAdmissionID;
            transferFromBedID;
            transferToBedID;
            transferDate;
            transferReason;
            transferUserID;
        FROM transfers
        WHERE transferID = ?;
    """;

    private final JdbcTemplate jdbcTemplate;

    public TransferRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transfer> transferRowMapper = (rs, rowNum) -> {
        Transfer i = new Transfer();
        
        // Map columns to model properties
        i.transferID = rs.getLong("transferID");
        i.transferAdmissionID = rs.getLong("transferAdmissionID");

        i.transferFromBedID = rs.getLong("transferFromBedID");
        i.transferToBedID = rs.getLong("transferToBedID");

        // Safe parsing for transferInDate
        String td = rs.getString("transferDate");
        if (td != null && !td.isEmpty()) {
            i.transferDate = LocalDateTime.parse(td);
        } else {
            i.transferDate = null;
        }

        i.transferReason = rs.getString("transferReason");
        i.transferUserID = rs.getLong("transferUserID");

        return i;
    };

    public List<Transfer> getAllTransfers() {
        return jdbcTemplate.query(FIND_ALL_TRANSFERS, transferRowMapper);
    }

    public Transfer getTransferByID(Long id) {
        return jdbcTemplate.query(FIND_TRANSFER_BY_ID, id);
    }
}
