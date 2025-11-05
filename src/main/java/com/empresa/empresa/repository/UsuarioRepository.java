package com.empresa.empresa.repository;

import com.empresa.empresa.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su nombre de usuario
     * @param username el nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     * @param username el nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    Optional<Usuario> findByResetToken(String resetToken);
}