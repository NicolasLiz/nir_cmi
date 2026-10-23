package br.com.nircmi.controller;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    // Key = Access Token String, Value = User ID UUID
    private final Map<String, UUID> activeUserTokens = new ConcurrentHashMap<>();

    public void addToken(String token, UUID userId) {
        activeUserTokens.put(token, userId);
    }

    public boolean isValidToken(String token) {
        return token != null && activeUserTokens.containsKey(token);
    }

    public void removeToken(String token) {
        activeUserTokens.remove(token);
    }
}
