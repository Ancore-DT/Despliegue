package com.empresa.empresa.config;

import com.empresa.empresa.entity.Usuario;
import com.empresa.empresa.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos para crear usuarios por defecto al iniciar la aplicación
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador por defecto si no existe
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario("admin", passwordEncoder.encode("admin123"));
            admin.agregarRol("ADMIN");
            admin.agregarRol("USER");
            usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado: admin / admin123");
        }

        // Crear usuario normal por defecto si no existe
        if (!usuarioRepository.existsByUsername("user")) {
            Usuario user = new Usuario("user", passwordEncoder.encode("user123"));
            user.agregarRol("USER");
            usuarioRepository.save(user);
            System.out.println("Usuario normal creado: user / user123");
        }
    }
}