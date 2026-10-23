package br.com.nircmi.dto;

/**
 * Equivalente ao corpo do POST /api/zebra/wristband em server/index.js
 */
public class PacienteWristbandRequest {
    private String nome;
    private String codigoPaciente;
    private String codigoAtendimento;
    private String dataNascimento;
    private String sexo;
    private String unidade;
    private String leito;
    private String codigoBarras;
    private String nomeMae;
    private String prevAlta;

    private WristbandSettings settings;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigoPaciente() { return codigoPaciente; }
    public void setCodigoPaciente(String codigoPaciente) { this.codigoPaciente = codigoPaciente; }

    public String getCodigoAtendimento() { return codigoAtendimento; }
    public void setCodigoAtendimento(String codigoAtendimento) { this.codigoAtendimento = codigoAtendimento; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getLeito() { return leito; }
    public void setLeito(String leito) { this.leito = leito; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNomeMae() { return nomeMae; }
    public void setNomeMae(String nomeMae) { this.nomeMae = nomeMae; }

    public String getPrevAlta() { return prevAlta; }
    public void setPrevAlta(String prevAlta) { this.prevAlta = prevAlta; }

    public WristbandSettings getSettings() { return settings; }
    public void setSettings(WristbandSettings settings) { this.settings = settings; }
}
