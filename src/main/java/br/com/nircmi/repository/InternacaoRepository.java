package br.com.nircmi.repository;

import br.com.nircmi.model.Internacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

/**
 * Equivalente a server/queries.js (getCmiInternacoes).
 */
@Repository
public class InternacaoRepository {
    private static final String CMI_INTERNACOES_SQL = """
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

    private final JdbcTemplate jdbcTemplate;

    public InternacaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Internacao> findCmiInternacoes() {
        return jdbcTemplate.query(CMI_INTERNACOES_SQL, (rs, rowNum) -> {
            Internacao i = new Internacao();
            i.setCdUnidInt(rs.getString("cdUnidInt"));
            i.setDsUnidInt(rs.getString("dsUnidInt"));
            i.setCdAtendimento(rs.getLong("cdAtendimento"));
            i.setCdPaciente(rs.getLong("cdPaciente"));
            i.setNmPaciente(rs.getString("nmPaciente"));
            i.setLeito(rs.getString("leito"));
            String prevAltaStr = rs.getString("prevAlta");
            if (prevAltaStr != null) {
                // SQLite strings might need standard formatting or DateTimeFormatter if they contain fractional seconds
                i.setPrevAlta(LocalDateTime.parse(prevAltaStr.replace(" ", "T")));
            }
            i.setDiasInternado(rs.getLong("diasInternado"));
            i.setAlertaDias(rs.getString("alertaDias"));
            return i;
        });
    }
}
