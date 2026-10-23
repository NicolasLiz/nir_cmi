package br.com.nircmi.dto;

/**
 * corpo de pedidos que envolvem dados do usuario do sistema(pode conter campos vazios)
 */
public class UserRequest {
    public String userName;
    public String userEmail;
    public String userPassword; //never stored or used raw, only hashed with sha-256(only raw when sent by client)
}
