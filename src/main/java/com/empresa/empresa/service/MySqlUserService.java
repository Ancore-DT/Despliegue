package com.empresa.empresa.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MySqlUserService {

    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public MySqlUserService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void changePassword(String username, String rawPassword) {
        String encoded = encoder.encode(rawPassword);
        int updated = jdbc.update("UPDATE users SET password = ? WHERE username = ?", encoded, username);
        if (updated == 0) {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }
    }
}