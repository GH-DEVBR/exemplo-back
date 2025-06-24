package com.seusite.backend.security;


import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        String key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("CHAVE SEGURA: " + key);
    }
}

