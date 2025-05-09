package com.imbus.knowledge;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        // Generate a key for HS256 (requires 256 bits = 32 bytes)
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // Convert the key to a Base64 encoded string
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated SECRET_KEY: " + base64Key);
    }
}
