package br.com.nircmi.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    // 64 bit flags for permisions
    public static final long uPermSystemParameters = 1L << 0;
    public static final long uPermReadAudits = 1L << 1;

    public static final long uPermRegisterUser = 1L << 2;
    public static final long uPermUpdateUser = 1L << 3;
    public static final long uPermDeactivateUser = 1L << 4;
    public static final long uPermSetUserPermissions = 1L << 5;

    public static final long uPermRegisterSector = 1L << 6;
    public static final long uPermViewSector = 1L << 7;
    public static final long uPermUpdateSector = 1L << 8;

    public static final long uPermRegisterRoom = 1L << 9;
    public static final long uPermViewRoom = 1L << 10;
    public static final long uPermUpdateRoom = 1L << 11;

    public static final long uPermRegisterBed = 1L << 12;
    public static final long uPermViewBed = 1L << 13;
    public static final long uPermUpdateBed = 1L << 14;
    public static final long uPermAllocateBed = 1L << 15;

    public static final long uPermRegisterPatient = 1L << 16;
    public static final long uPermViewPatient = 1L << 17;
    public static final long uPermFindPatient = 1L << 18;
    public static final long uPermTransferPatient = 1L << 19;
    public static final long uPermInternPatient = 1L << 20;
    public static final long uPermFreePatient = 1L << 21;
    public static final long uPermViewIndicators = 1L << 22;

    public static final long uPermCreateReport = 1L << 23;



    public static final long uTypeAdmin = ~0L;
    public static final long uTypeManagement = (
        uPermViewSector |
        uPermViewRoom |
        uPermViewBed |
        uPermViewPatient |
        uPermViewIndicators |
        uPermCreateReport
    );
    public static final long uTypeReception = (
        uPermRegisterPatient |
        uPermFindPatient |
        uPermInternPatient |
        uPermAllocateBed |
        uPermViewBed |
        uPermFreePatient
    );
    public static final long uTypeNurse = (
        uPermViewBed |
        uPermViewPatient |
        uPermTransferPatient |
        uPermUpdateBed
    );
    public static final long uTypeMedic = (
        uPermViewPatient |
        uPermViewSector |
        uPermViewBed
    );

    public UUID userID;
    public String userName;
    public String userEmail;
    public String userPassword; //never stored or used raw, only hashed with sha-256
    public Long userPermissions; // use the flag constants to assign/mask/compare
    public int userActive; // 0 false 1 true( >:( java)
    public LocalDateTime userRegisterDate;
}

// Permissões:
// SISTEMA
// * Configurar parâmetros do sistema.
// * Consultar registros de auditoria.
// *
// USUARIOS
// * Cadastrar usuários.
// * Alterar usuários.
// * Inativar usuários.
// * Definir perfis de acesso.
// *
// SETORES
// * Cadastrar setores.
// * Visualizar todos os setores.
// *
// QUARTOS
// * Cadastrar quartos.
// * Consultar ocupação.
// * Consultar disponibilidade.
// * Consultar informações relacionadas à internação.
// * Consultar movimentações.
// *
// LEITOS
// * Cadastrar leitos.
// * Consultar leitos.
// * Atualizar situação operacional do leito.
// * Consultar situação dos leitos.
// * Solicitar/alocar leitos disponíveis.
// * Visualizar pacientes vinculadas aos leitos.
// *
// PACIENTES
// * Cadastrar pacientes.
// * Consultar pacientes.
// * Localizar pacientes.
// * Consultar pacientes internadas.
// * Solicitar transferência.
// * Registrar movimentações permitidas.
// * Registrar internações.
// * Registrar alta conforme permissão definida.
// * Consultar movimentações.
// * Gerar relatórios.
// * Visualizar indicadores.
//
//
//
// Administrador
// Permissões:
// * Cadastrar usuários.
// * Alterar usuários.
// * Inativar usuários.
// * Definir perfis de acesso.
// * Cadastrar setores.
// * Cadastrar quartos.
// * Cadastrar leitos.
// * Configurar parâmetros do sistema.
// * Consultar registros de auditoria.
//
// Gestão/Coordenação
// Permissões:
// * Visualizar todos os setores.
// * Consultar disponibilidade.
// * Consultar ocupação.
// * Consultar pacientes internadas.
// * Consultar movimentações.
// * Gerar relatórios.
// * Visualizar indicadores.
//
// Recepção/Internação
// Permissões:
// * Cadastrar pacientes.
// * Localizar pacientes.
// * Registrar internações.
// * Solicitar/alocar leitos disponíveis.
// * Consultar situação dos leitos.
// * Registrar alta conforme permissão definida.
//
// Enfermagem
// Permissões:
// * Consultar leitos.
// * Visualizar pacientes vinculadas aos leitos.
// * Solicitar transferência.
// * Registrar movimentações permitidas.
// * Atualizar situação operacional do leito.
//
// Médico
// Permissões:
// * Consultar pacientes.
// * Consultar setor e leito.
// * Consultar informações relacionadas à internação.
// * Consultar movimentações.
