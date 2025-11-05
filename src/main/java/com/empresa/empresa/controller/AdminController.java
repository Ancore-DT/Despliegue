package com.empresa.empresa.controller;

import com.empresa.empresa.entity.Usuario;
import com.empresa.empresa.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página principal de administración
     */
    @GetMapping
    public String mostrarPanelAdmin() {
        return "admin/panel";
    }

    /**
     * Muestra la lista de usuarios
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        return "admin/usuarios";
    }

    /**
     * Cambia el estado de un usuario (activo/inactivo)
     */
    @PostMapping("/usuarios/{id}/toggle-estado")
    public String cambiarEstadoUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Cambiar el estado del usuario
            usuario.setActivo(!usuario.isActivo());
            usuarioService.guardar(usuario);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                    "Estado del usuario '" + usuario.getUsername() + "' actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }

    /**
     * Elimina un usuario
     */
    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // No permitir eliminar al usuario actual
            if (usuario.getUsername().equals("admin")) {
                redirectAttributes.addFlashAttribute("error", "No se puede eliminar el usuario administrador principal");
                return "redirect:/admin/usuarios";
            }
            
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }
}