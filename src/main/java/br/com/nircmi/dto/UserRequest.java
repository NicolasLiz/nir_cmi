package br.com.nircmi.dto;

/**
 * corpo de pedidos que envolvem dados do usuarion do sistema
 */
public class UserRequest {
    public String userName;
    public String userEmail;
    public String userPassword; //never stored or used raw, only hashed with sha-256(only raw when sent by client)
}
