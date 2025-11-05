package com.empresa.empresa.service;

import com.empresa.empresa.entity.Usuario;
import com.empresa.empresa.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Carga un usuario por su nombre de usuario para la autenticación
     * @param username el nombre de usuario
     * @return UserDetails para Spring Security
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar el usuario en la tabla usuarios de MySQL
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Convertir roles a autoridades de Spring Security
        List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                .collect(Collectors.toList());

        // Crear un UserDetails con la información del usuario de la base de datos
        return new User(usuario.getUsername(), usuario.getPassword(), usuario.isActivo(),
                true, true, true, authorities);
    }

    /**
     * Registra un nuevo usuario en el sistema
     * @param usuario el usuario a registrar
     * @return el usuario registrado
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Codificar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Si no se especificaron roles, asignar rol por defecto
        if (usuario.getRoles().isEmpty()) {
            usuario.agregarRol("USER");
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene todos los usuarios del sistema
     * @return lista de usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su ID
     * @param id el ID del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca un usuario por su nombre de usuario
     * @param username el nombre de usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Elimina un usuario por su ID
     * @param id el ID del usuario a eliminar
     */
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Guarda un usuario existente
     * @param usuario el usuario a guardar
     * @return el usuario guardado
     */
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Genera un token de restablecimiento para el usuario y lo guarda con expiración
     */
    public String generarResetToken(String username) {
        Optional<Usuario> uOpt = usuarioRepository.findByUsername(username);
        if (uOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        Usuario usuario = uOpt.get();
        String token = java.util.UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(java.time.LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);
        return token;
    }

    public Optional<Usuario> buscarPorResetToken(String token) {
        return usuarioRepository.findByResetToken(token);
    }

    public void resetearContrasena(String token, String nuevaContrasena) {
        Optional<Usuario> uOpt = usuarioRepository.findByResetToken(token);
        if (uOpt.isEmpty()) {
            throw new RuntimeException("Token inválido");
        }
        Usuario usuario = uOpt.get();
        if (usuario.getResetTokenExpiry() == null || usuario.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }
        usuario.setPassword(passwordEncoder.encode(nuevaContrasena));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        usuarioRepository.save(usuario);
    }
}