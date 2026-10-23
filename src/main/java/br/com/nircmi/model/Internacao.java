package br.com.nircmi.model;

import java.time.LocalDateTime;

/**
 * DTO equivalente as linhas retornadas por getCmiInternacoes() em server/queries.js
 */
public class Internacao {
    private String cdUnidInt;
    private String dsUnidInt;
    private Long cdAtendimento;
    private Long cdPaciente;
    private String nmPaciente;
    private String leito;
    private LocalDateTime prevAlta;
    private Long diasInternado;
    private String alertaDias;

    public String getCdUnidInt() { return cdUnidInt; }
    public void setCdUnidInt(String cdUnidInt) { this.cdUnidInt = cdUnidInt; }

    public String getDsUnidInt() { return dsUnidInt; }
    public void setDsUnidInt(String dsUnidInt) { this.dsUnidInt = dsUnidInt; }

    public Long getCdAtendimento() { return cdAtendimento; }
    public void setCdAtendimento(Long cdAtendimento) { this.cdAtendimento = cdAtendimento; }

    public Long getCdPaciente() { return cdPaciente; }
    public void setCdPaciente(Long cdPaciente) { this.cdPaciente = cdPaciente; }

    public String getNmPaciente() { return nmPaciente; }
    public void setNmPaciente(String nmPaciente) { this.nmPaciente = nmPaciente; }

    public String getLeito() { return leito; }
    public void setLeito(String leito) { this.leito = leito; }

    public LocalDateTime getPrevAlta() { return prevAlta; }
    public void setPrevAlta(LocalDateTime prevAlta) { this.prevAlta = prevAlta; }

    public Long getDiasInternado() { return diasInternado; }
    public void setDiasInternado(Long diasInternado) { this.diasInternado = diasInternado; }

    public String getAlertaDias() { return alertaDias; }
    public void setAlertaDias(String alertaDias) { this.alertaDias = alertaDias; }
}
