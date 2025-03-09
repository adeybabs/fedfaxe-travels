package com.project.fedfaxe.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureKeyGenerator {

    public static void main(String[] args) {
        byte[] key = new byte[32]; // 256 bits (32 bytes)
        new SecureRandom().nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println("Secure Key: " + encodedKey);
    }
}
