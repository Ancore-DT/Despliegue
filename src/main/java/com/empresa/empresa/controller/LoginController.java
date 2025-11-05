package com.empresa.empresa.controller;

import com.empresa.empresa.entity.Usuario;
import com.empresa.empresa.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de login
     * @param model el modelo para la vista
     * @return la vista de login
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        // Si el usuario ya está autenticado, redirigir a la página principal
        if (estaAutenticado()) {
            return "redirect:/";
        }
        return "login";
    }

    /**
     * Muestra la página de registro
     * @param model el modelo para la vista
     * @return la vista de registro
     */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        // Si el usuario ya está autenticado, redirigir a la página principal
        if (estaAutenticado()) {
            return "redirect:/";
        }
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    /**
     * Procesa el formulario de registro
     * @param usuario el usuario a registrar
     * @param result resultado de la validación
     * @param model el modelo para la vista
     * @param redirectAttributes atributos para redirección
     * @return redirección a login o vista de registro con errores
     */
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("usuario") Usuario usuario,
                                  BindingResult result, Model model,
                                  RedirectAttributes redirectAttributes) {
        // Verificar errores de validación
        if (result.hasErrors()) {
            return "registro";
        }

        try {
            // Intentar registrar el usuario
            usuarioService.registrarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso. Ahora puede iniciar sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            // Manejar errores (por ejemplo, nombre de usuario duplicado)
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    /**
     * Muestra la página de acceso denegado
     * @return la vista de acceso denegado
     */
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }

    // Mostrar formulario para solicitar restablecimiento de contraseña
    @GetMapping("/olvidaste-contrasena")
    public String mostrarOlvido() {
        return "olvido-contrasena";
    }

    // Procesar solicitud de restablecimiento (genera token y muestra mensaje)
    @PostMapping("/olvidaste-contrasena")
    public String procesarOlvido(String username, RedirectAttributes redirectAttributes, Model model) {
        try {
            String token = usuarioService.generarResetToken(username);
            // En producción enviar por email; aquí mostraremos mensaje simple
            redirectAttributes.addFlashAttribute("mensaje", "Se generó un token de restablecimiento. Use el enlace /reset-password?token=" + token);
            return "redirect:/olvidaste-contrasena";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "olvido-contrasena";
        }
    }

    // Mostrar formulario para restablecer contraseña (token en query param)
    @GetMapping("/reset-password")
    public String mostrarReset(String token, Model model) {
        model.addAttribute("token", token);
        return "reset-contrasena";
    }

    // Procesar restablecimiento
    @PostMapping("/reset-password")
    public String procesarReset(String token, String password, String confirm, Model model, RedirectAttributes redirectAttributes) {
        if (password == null || !password.equals(confirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("token", token);
            return "reset-contrasena";
        }
        try {
            usuarioService.resetearContrasena(token, password);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña restablecida correctamente. Ahora puede iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "reset-contrasena";
        }
    }

    /**
     * Verifica si el usuario actual está autenticado
     * @return true si está autenticado, false en caso contrario
     */
    private boolean estaAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication instanceof AnonymousAuthenticationToken);
    }
}