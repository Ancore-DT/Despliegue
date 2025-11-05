package com.empresa.empresa.controller;

import com.empresa.empresa.entity.Empleado;
import com.empresa.empresa.service.EmpleadoService;
import com.empresa.empresa.service.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {
    
    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private ProyectoService proyectoService;
    
    // Listar todos los empleados
    @GetMapping
    public String listarEmpleados(Model model, @RequestParam(required = false) String busqueda) {
        if (busqueda != null && !busqueda.isEmpty()) {
            model.addAttribute("empleados", empleadoService.buscarPorNombreOCargo(busqueda));
            model.addAttribute("busqueda", busqueda);
        } else {
            model.addAttribute("empleados", empleadoService.obtenerTodos());
        }
        return "empleados/lista";
    }
    
    // Mostrar formulario para crear un nuevo empleado
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("empleado", new Empleado());
        return "empleados/formulario";
    }
    
    // Guardar un nuevo empleado
    @PostMapping("/guardar")
    public String guardarEmpleado(@Valid @ModelAttribute Empleado empleado, BindingResult result, RedirectAttributes redirectAttributes) {
        // Validar que el email no esté duplicado
        if (empleado.getId() == null && empleadoService.existeEmail(empleado.getEmail())) {
            result.rejectValue("email", "error.empleado", "Este email ya está registrado");
        }
        
        if (result.hasErrors()) {
            return "empleados/formulario";
        }
        
        empleadoService.guardar(empleado);
        redirectAttributes.addFlashAttribute("mensaje", "Empleado guardado correctamente");
        return "redirect:/empleados";
    }
    
    // Mostrar formulario para editar un empleado
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        empleadoService.obtenerPorId(id).ifPresent(empleado -> model.addAttribute("empleado", empleado));
        return "empleados/formulario";
    }
    
    // Eliminar un empleado
    @PostMapping("/eliminar/{id}")
    public String eliminarEmpleadoPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        empleadoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Empleado eliminado correctamente");
        return "redirect:/empleados";
    }
    
    // Eliminar un empleado via AJAX
    @DeleteMapping("/eliminar/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarEmpleadoAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            empleadoService.eliminar(id);
            response.put("success", true);
            response.put("mensaje", "Empleado eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al eliminar el empleado: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Ver detalles de un empleado y sus proyectos
    @GetMapping("/detalle/{id}")
    public String verDetalleEmpleado(@PathVariable Long id, Model model) {
        empleadoService.obtenerPorId(id).ifPresent(empleado -> {
            model.addAttribute("empleado", empleado);
            model.addAttribute("proyectos", proyectoService.buscarPorEmpleadoId(id));
        });
        return "empleados/detalle";
    }
}
