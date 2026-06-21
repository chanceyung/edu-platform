package com.eduplatform.ruoyibase;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordGenTest {
    @Test
    void generateAdminPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println("=== BCRYPT HASH ===");
        System.out.println(hash);
        System.out.println("=== VERIFY ===");
        System.out.println("matches: " + encoder.matches("admin123", hash));
    }
}
