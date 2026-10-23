package br.com.nircmi.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 */
public class User {
    // 64 bit flags for permisions
    static public final Long UserPermissionFlagNone = 0b0L;
    static public final Long UserPermissionFlagAll = ~0b0L;

    public UUID userID;
    public String userName;
    public String userEmail;
    public String userPassword; //never stored or used raw, only hashed with sha-256
    public Long userPermissions; // use the flag constants to assign/mask/compare
}
