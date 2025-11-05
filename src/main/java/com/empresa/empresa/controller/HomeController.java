package com.empresa.empresa.controller;

import com.empresa.empresa.service.EmpleadoService;
import com.empresa.empresa.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página principal y dashboard
 */
@Controller
public class HomeController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ProyectoService proyectoService;

    /**
     * Muestra la página principal con información del dashboard
     * @param model el modelo para la vista
     * @return la vista index
     */
    @GetMapping("/")
    public String index(Model model) {
        // Agregar contadores al dashboard
        model.addAttribute("totalEmpleados", empleadoService.obtenerTodos().size());
        model.addAttribute("totalProyectos", proyectoService.obtenerTodos().size());
        
        return "index";
    }

    /**
     * Redirecciona a la página principal
     * @return redirección a la página principal
     */
    @GetMapping("/home")
    public String home() {
        return "redirect:/";
    }
}