package br.com.nircmi.dto;

import java.util.UUID;

/**
 * corpo de pedidos que envolvem dados do usuario do sistema(pode conter campos vazios)
 */
public class UserRequest {
    public UUID userID;
    public String userName;
    public String userEmail;
    public String userPassword; //never stored or used raw, only hashed with sha-256(only raw when sent by client)
    public Long userPermissions;
    public int userActive;
}
