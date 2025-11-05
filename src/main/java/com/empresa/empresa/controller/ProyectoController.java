package com.empresa.empresa.controller;

import com.empresa.empresa.entity.Empleado;
import com.empresa.empresa.entity.Proyecto;
import com.empresa.empresa.entity.Tarea;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/proyectos")
public class ProyectoController {
    
    @Autowired
    private ProyectoService proyectoService;
    
    @Autowired
    private EmpleadoService empleadoService;
    
    // Listar todos los proyectos
    @GetMapping
    public String listarProyectos(Model model, 
                                 @RequestParam(required = false) String nombre,
                                 @RequestParam(required = false) String estado) {
        List<Proyecto> proyectos;
        
        if (nombre != null && !nombre.isEmpty()) {
            proyectos = proyectoService.buscarPorNombre(nombre);
        } else {
            proyectos = proyectoService.obtenerTodos();
        }
        
        // Filtrar por estado si se especificó
        if (estado != null && !estado.isEmpty()) {
            proyectos = proyectos.stream()
                .filter(p -> p.getEstado().equals(estado))
                .toList();
        }
        
        model.addAttribute("proyectos", proyectos);
        return "proyectos/lista";
    }
    
    // Mostrar formulario para crear un nuevo proyecto
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("proyecto", new Proyecto());
        model.addAttribute("empleados", empleadoService.obtenerTodos());
        return "proyectos/formulario";
    }
    
    // Guardar un nuevo proyecto
    @PostMapping("/guardar")
    public String guardarProyecto(@Valid @ModelAttribute Proyecto proyecto,
                                BindingResult result,
                                @RequestParam(required = false) List<Long> empleadosIds,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("empleados", empleadoService.obtenerTodos());
            return "proyectos/formulario";
        }
        
        // Establecer fecha de creación
        if (proyecto.getFechaCreacion() == null) {
            proyecto.setFechaCreacion(LocalDateTime.now());
        }
        
        // Asignar empleados al proyecto
        if (empleadosIds != null && !empleadosIds.isEmpty()) {
            List<Empleado> empleadosAsignados = new ArrayList<>();
            for (Long empleadoId : empleadosIds) {
                empleadoService.obtenerPorId(empleadoId).ifPresent(empleadosAsignados::add);
            }
            proyecto.setEmpleados(empleadosAsignados);
        }
        
        proyectoService.guardar(proyecto);
        redirectAttributes.addFlashAttribute("mensaje", "Proyecto guardado correctamente");
        return "redirect:/proyectos";
    }
    
    // Mostrar formulario para editar un proyecto
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Proyecto proyecto = proyectoService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("empleados", empleadoService.obtenerTodos());
            return "proyectos/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el proyecto: " + e.getMessage());
            return "redirect:/proyectos";
        }
    }
    
    // Actualizar un proyecto existente
    @PostMapping("/{id}/actualizar")
    public String actualizarProyecto(@PathVariable String id,
                                   @Valid @ModelAttribute Proyecto proyecto,
                                   BindingResult result,
                                   @RequestParam(required = false) List<Long> empleadosIds,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("empleados", empleadoService.obtenerTodos());
            return "proyectos/formulario";
        }
        
        try {
            // Verificar que el proyecto existe
            proyectoService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            
            // Asignar empleados al proyecto
            if (empleadosIds != null) {
                List<Empleado> empleadosAsignados = new ArrayList<>();
                for (Long empleadoId : empleadosIds) {
                    empleadoService.obtenerPorId(empleadoId).ifPresent(empleadosAsignados::add);
                }
                proyecto.setEmpleados(empleadosAsignados);
            } else {
                proyecto.setEmpleados(new ArrayList<>());
            }
            
            proyectoService.guardar(proyecto);
            redirectAttributes.addFlashAttribute("mensaje", "Proyecto actualizado correctamente");
            return "redirect:/proyectos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el proyecto: " + e.getMessage());
            return "redirect:/proyectos";
        }
    }

    // Eliminar un proyecto
    @PostMapping("/{id}/eliminar")
    public String eliminarProyecto(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            proyectoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Proyecto eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el proyecto: " + e.getMessage());
        }
        return "redirect:/proyectos";
    }
    
    // Eliminar un proyecto via AJAX
    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarProyectoAjax(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            proyectoService.eliminar(id);
            response.put("success", true);
            response.put("mensaje", "Proyecto eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al eliminar el proyecto: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Fallback: permitir también POST a /api/eliminar/{id} para clientes AJAX que no puedan usar DELETE
    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarProyectoApiPost(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            proyectoService.eliminar(id);
            response.put("success", true);
            response.put("mensaje", "Proyecto eliminado correctamente (POST API)");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al eliminar el proyecto: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Ver detalles de un proyecto
    @GetMapping("/{id}")
    public String verDetalleProyecto(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Proyecto proyecto = proyectoService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("nuevaTarea", new Tarea());
            return "proyectos/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el proyecto: " + e.getMessage());
            return "redirect:/proyectos";
        }
    }
    
    // Agregar una tarea a un proyecto
    @PostMapping("/{id}/tareas/agregar")
    public String agregarTarea(@PathVariable String id,
                             @Valid @ModelAttribute("nuevaTarea") Tarea tarea,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            try {
                Proyecto proyecto = proyectoService.obtenerPorId(id)
                        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
                
                model.addAttribute("proyecto", proyecto);
                return "proyectos/detalle";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al cargar el proyecto: " + e.getMessage());
                return "redirect:/proyectos";
            }
        }
        
        try {
            proyectoService.agregarTarea(id, tarea);
            redirectAttributes.addFlashAttribute("mensaje", "Tarea agregada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar la tarea: " + e.getMessage());
        }
        
        return "redirect:/proyectos/" + id;
    }
    
    // Marcar una tarea como completada
    @PostMapping("/{proyectoId}/tareas/{tareaIndex}/completar")
    public String completarTarea(@PathVariable String proyectoId,
                               @PathVariable int tareaIndex,
                               RedirectAttributes redirectAttributes) {
        try {
            Proyecto proyecto = proyectoService.obtenerPorId(proyectoId)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            
            if (tareaIndex >= 0 && tareaIndex < proyecto.getTareas().size()) {
                Tarea tarea = proyecto.getTareas().get(tareaIndex);
                tarea.setCompletada(!tarea.isCompletada());
                proyectoService.guardar(proyecto);
                redirectAttributes.addFlashAttribute("mensaje", "Estado de la tarea actualizado");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la tarea: " + e.getMessage());
        }
        
        return "redirect:/proyectos/" + proyectoId;
    }

    // Eliminar una tarea (por id o por índice)
    @PostMapping("/{proyectoId}/tareas/{tareaId}/eliminar")
    public String eliminarTarea(@PathVariable String proyectoId,
                                @PathVariable String tareaId,
                                RedirectAttributes redirectAttributes) {
        try {
            Proyecto proyecto = proyectoService.obtenerPorId(proyectoId)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

            boolean removed = false;
            // Primero intentar eliminar por id (campo id de la tarea)
            if (proyecto.getTareas() != null) {
                removed = proyecto.getTareas().removeIf(t -> t.getId() != null && t.getId().equals(tareaId));
            }

            // Si no se eliminó por id, intentar interpretar tareaId como índice
            if (!removed) {
                try {
                    int index = Integer.parseInt(tareaId);
                    if (index >= 0 && index < proyecto.getTareas().size()) {
                        proyecto.getTareas().remove(index);
                        removed = true;
                    }
                } catch (NumberFormatException ignored) {
                    // no es un índice
                }
            }

            if (removed) {
                proyectoService.guardar(proyecto);
                redirectAttributes.addFlashAttribute("mensaje", "Tarea eliminada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la tarea: " + e.getMessage());
        }

        return "redirect:/proyectos/" + proyectoId;
    }
}
