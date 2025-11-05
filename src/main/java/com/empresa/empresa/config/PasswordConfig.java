package com.empresa.empresa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración separada para el PasswordEncoder para evitar dependencias circulares
 */
@Configuration
public class PasswordConfig {

    /**
     * Configura el codificador de contraseñas
     * @return PasswordEncoder para codificar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}