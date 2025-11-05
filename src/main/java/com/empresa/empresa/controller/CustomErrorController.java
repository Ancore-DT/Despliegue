package com.empresa.empresa.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para manejar errores de la aplicación
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Maneja los errores y muestra una página de error personalizada
     * @param request la solicitud HTTP
     * @param model el modelo para la vista
     * @return la vista de error
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Obtener el código de estado del error
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = 500; // Por defecto, error interno del servidor
        
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }
        
        // Agregar información del error al modelo
        model.addAttribute("codigo", statusCode);
        
        // Personalizar el mensaje según el código de estado
        String mensaje;
        switch (statusCode) {
            case 404:
                mensaje = "Página no encontrada";
                break;
            case 403:
                mensaje = "Acceso denegado";
                break;
            case 500:
                mensaje = "Error interno del servidor";
                break;
            default:
                mensaje = "Se ha producido un error";
        }
        
        model.addAttribute("mensaje", mensaje);
        
        // Devolver la vista de error
        return "error";
    }
}