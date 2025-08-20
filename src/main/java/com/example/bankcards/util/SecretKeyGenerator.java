package com.example.bankcards.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class SecretKeyGenerator {

    public static void main(String[] args) {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Секретный ключ: " + secretString);
    }
}
