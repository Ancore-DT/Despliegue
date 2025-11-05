package com.empresa.empresa.controller.api;

import com.empresa.empresa.entity.Proyecto;
import com.empresa.empresa.entity.Tarea;
import com.empresa.empresa.service.ProyectoService;
import com.empresa.empresa.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST API para gestión de Proyectos (MongoDB)
 * Base URL: /api/proyectos
 */
@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "*") // Permitir CORS para pruebas
public class ProyectoRestController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private EmpleadoService empleadoService;

    /**
     * GET /api/proyectos
     * Obtener todos los proyectos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long empleadoId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Proyecto> proyectos = proyectoService.obtenerTodos();

            // Filtrar por estado si se proporciona
            if (estado != null && !estado.isEmpty()) {
                proyectos = proyectos.stream()
                    .filter(p -> p.getEstado().equalsIgnoreCase(estado))
                    .collect(Collectors.toList());
            }

            // Filtrar por empleadoId si se proporciona
            if (empleadoId != null) {
                proyectos = proyectos.stream()
                    .filter(p -> empleadoId.equals(p.getEmpleadoId()))
                    .collect(Collectors.toList());
            }

            response.put("success", true);
            response.put("data", proyectos);
            response.put("total", proyectos.size());
            response.put("message", "Proyectos obtenidos correctamente");
            
            if (estado != null) response.put("filtroEstado", estado);
            if (empleadoId != null) response.put("filtroEmpleadoId", empleadoId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener proyectos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/proyectos/{id}
     * Obtener un proyecto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Proyecto> proyecto = proyectoService.obtenerPorId(id);
            if (proyecto.isPresent()) {
                Proyecto p = proyecto.get();
                response.put("success", true);
                response.put("data", p);
                response.put("porcentajeProgreso", p.getPorcentajeCompletado());
                response.put("tareasCompletadas", p.getTareasCompletadas());
                response.put("totalTareas", p.getTareas().size());
                response.put("message", "Proyecto encontrado");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener proyecto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/proyectos
     * Crear un nuevo proyecto
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Proyecto proyecto, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        
        // Validar errores de validación
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errores.put(error.getField(), error.getDefaultMessage())
            );
            response.put("success", false);
            response.put("message", "Errores de validación");
            response.put("errors", errores);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Validar que el empleadoId exista si se proporciona
            if (proyecto.getEmpleadoId() != null) {
                if (!empleadoService.obtenerPorId(proyecto.getEmpleadoId()).isPresent()) {
                    response.put("success", false);
                    response.put("message", "Empleado no encontrado con ID: " + proyecto.getEmpleadoId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }

            // Asegurar valores por defecto
            if (proyecto.getFechaCreacion() == null) {
                proyecto.setFechaCreacion(LocalDateTime.now());
            }
            if (proyecto.getEstado() == null || proyecto.getEstado().isEmpty()) {
                proyecto.setEstado("Pendiente");
            }
            if (proyecto.getTareas() == null) {
                proyecto.setTareas(new ArrayList<>());
            }

            Proyecto proyectoGuardado = proyectoService.guardar(proyecto);
            response.put("success", true);
            response.put("data", proyectoGuardado);
            response.put("message", "Proyecto creado exitosamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear proyecto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/proyectos/{id}
     * Actualizar un proyecto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody Proyecto proyecto,
            BindingResult result) {
        
        Map<String, Object> response = new HashMap<>();

        // Validar errores de validación
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errores.put(error.getField(), error.getDefaultMessage())
            );
            response.put("success", false);
            response.put("message", "Errores de validación");
            response.put("errors", errores);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            Optional<Proyecto> proyectoExistente = proyectoService.obtenerPorId(id);
            if (!proyectoExistente.isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validar que el empleadoId exista si se proporciona
            if (proyecto.getEmpleadoId() != null) {
                if (!empleadoService.obtenerPorId(proyecto.getEmpleadoId()).isPresent()) {
                    response.put("success", false);
                    response.put("message", "Empleado no encontrado con ID: " + proyecto.getEmpleadoId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }

            proyecto.setId(id);
            Proyecto proyectoActualizado = proyectoService.guardar(proyecto);
            response.put("success", true);
            response.put("data", proyectoActualizado);
            response.put("message", "Proyecto actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar proyecto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/proyectos/{id}
     * Eliminar un proyecto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Proyecto> proyecto = proyectoService.obtenerPorId(id);
            if (!proyecto.isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            proyectoService.eliminar(id);
            response.put("success", true);
            response.put("message", "Proyecto eliminado exitosamente");
            response.put("data", proyecto.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar proyecto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/proyectos/buscar?nombre={texto}
     * Buscar proyectos por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String nombre) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Proyecto> proyectos = proyectoService.buscarPorNombre(nombre);
            response.put("success", true);
            response.put("data", proyectos);
            response.put("total", proyectos.size());
            response.put("message", "Búsqueda completada");
            response.put("termino", nombre);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/proyectos/empleado/{empleadoId}
     * Obtener proyectos de un empleado específico
     * Busca tanto en el campo empleadoId como en el array de empleados
     */
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<Map<String, Object>> obtenerPorEmpleado(@PathVariable Long empleadoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verificar que el empleado existe
            if (!empleadoService.obtenerPorId(empleadoId).isPresent()) {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + empleadoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Busca en ambos lugares: empleadoId directo y array empleados
            List<Proyecto> proyectos = proyectoService.buscarPorEmpleadoId(empleadoId);
            response.put("success", true);
            response.put("data", proyectos);
            response.put("total", proyectos.size());
            response.put("empleadoId", empleadoId);
            response.put("message", "Proyectos del empleado obtenidos correctamente");
            response.put("nota", "Busca en campo empleadoId y en array empleados[]");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener proyectos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PATCH /api/proyectos/{id}/estado
     * Actualizar solo el estado de un proyecto
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Proyecto> proyectoOpt = proyectoService.obtenerPorId(id);
            if (!proyectoOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            String nuevoEstado = body.get("estado");
            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El estado no puede estar vacío");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar estados permitidos
            List<String> estadosPermitidos = Arrays.asList("Pendiente", "En progreso", "Completado", "Cancelado");
            if (!estadosPermitidos.contains(nuevoEstado)) {
                response.put("success", false);
                response.put("message", "Estado no válido. Permitidos: " + estadosPermitidos);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Proyecto proyecto = proyectoOpt.get();
            proyecto.setEstado(nuevoEstado);
            proyectoService.guardar(proyecto);

            response.put("success", true);
            response.put("data", proyecto);
            response.put("message", "Estado actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/proyectos/{id}/tareas
     * Agregar una tarea a un proyecto
     */
    @PostMapping("/{id}/tareas")
    public ResponseEntity<Map<String, Object>> agregarTarea(
            @PathVariable String id,
            @Valid @RequestBody Tarea tarea,
            BindingResult result) {
        
        Map<String, Object> response = new HashMap<>();

        // Validar errores de validación
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errores.put(error.getField(), error.getDefaultMessage())
            );
            response.put("success", false);
            response.put("message", "Errores de validación");
            response.put("errors", errores);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            Proyecto proyecto = proyectoService.agregarTarea(id, tarea);
            if (proyecto == null) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", true);
            response.put("data", proyecto);
            response.put("tarea", tarea);
            response.put("message", "Tarea agregada exitosamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al agregar tarea: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/proyectos/{id}/tareas
     * Obtener todas las tareas de un proyecto
     */
    @GetMapping("/{id}/tareas")
    public ResponseEntity<Map<String, Object>> obtenerTareas(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Proyecto> proyectoOpt = proyectoService.obtenerPorId(id);
            if (!proyectoOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Proyecto proyecto = proyectoOpt.get();
            response.put("success", true);
            response.put("data", proyecto.getTareas());
            response.put("total", proyecto.getTareas().size());
            response.put("completadas", proyecto.getTareasCompletadas());
            response.put("porcentajeProgreso", proyecto.getPorcentajeCompletado());
            response.put("message", "Tareas obtenidas correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener tareas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PATCH /api/proyectos/{proyectoId}/tareas/{tareaId}/completar
     * Marcar una tarea como completada
     */
    @PatchMapping("/{proyectoId}/tareas/{tareaId}/completar")
    public ResponseEntity<Map<String, Object>> completarTarea(
            @PathVariable String proyectoId,
            @PathVariable String tareaId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Proyecto> proyectoOpt = proyectoService.obtenerPorId(proyectoId);
            if (!proyectoOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + proyectoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Proyecto proyecto = proyectoOpt.get();
            Optional<Tarea> tareaOpt = proyecto.getTareas().stream()
                .filter(t -> t.getId().equals(tareaId))
                .findFirst();

            if (!tareaOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Tarea no encontrada con ID: " + tareaId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Tarea tarea = tareaOpt.get();
            tarea.setCompletada(true);
            proyectoService.guardar(proyecto);

            response.put("success", true);
            response.put("data", proyecto);
            response.put("tarea", tarea);
            response.put("porcentajeProgreso", proyecto.getPorcentajeCompletado());
            response.put("message", "Tarea marcada como completada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al completar tarea: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/proyectos/{proyectoId}/tareas/{tareaId}
     * Eliminar una tarea de un proyecto
     */
    @DeleteMapping("/{proyectoId}/tareas/{tareaId}")
    public ResponseEntity<Map<String, Object>> eliminarTarea(
            @PathVariable String proyectoId,
            @PathVariable String tareaId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Proyecto> proyectoOpt = proyectoService.obtenerPorId(proyectoId);
            if (!proyectoOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + proyectoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Proyecto proyecto = proyectoOpt.get();
            Optional<Tarea> tareaOpt = proyecto.getTareas().stream()
                .filter(t -> t.getId().equals(tareaId))
                .findFirst();

            if (!tareaOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Tarea no encontrada con ID: " + tareaId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            proyecto.getTareas().removeIf(t -> t.getId().equals(tareaId));
            proyectoService.guardar(proyecto);

            response.put("success", true);
            response.put("data", proyecto);
            response.put("tareaEliminada", tareaOpt.get());
            response.put("message", "Tarea eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar tarea: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/proyectos/estadisticas
     * Obtener estadísticas generales de proyectos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Proyecto> proyectos = proyectoService.obtenerTodos();
            
            long totalProyectos = proyectos.size();
            long pendientes = proyectos.stream().filter(p -> "Pendiente".equals(p.getEstado())).count();
            long enProgreso = proyectos.stream().filter(p -> "En progreso".equals(p.getEstado())).count();
            long completados = proyectos.stream().filter(p -> "Completado".equals(p.getEstado())).count();
            long cancelados = proyectos.stream().filter(p -> "Cancelado".equals(p.getEstado())).count();
            
            long totalTareas = proyectos.stream().mapToLong(p -> p.getTareas().size()).sum();
            long tareasCompletadas = proyectos.stream().mapToLong(Proyecto::getTareasCompletadas).sum();

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalProyectos", totalProyectos);
            estadisticas.put("pendientes", pendientes);
            estadisticas.put("enProgreso", enProgreso);
            estadisticas.put("completados", completados);
            estadisticas.put("cancelados", cancelados);
            estadisticas.put("totalTareas", totalTareas);
            estadisticas.put("tareasCompletadas", tareasCompletadas);
            estadisticas.put("progresoGeneral", totalTareas > 0 ? (int)((tareasCompletadas * 100) / totalTareas) : 0);

            response.put("success", true);
            response.put("data", estadisticas);
            response.put("message", "Estadísticas obtenidas correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
