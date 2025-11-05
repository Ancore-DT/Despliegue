package com.empresa.empresa.controller.api;

import com.empresa.empresa.entity.Empleado;
import com.empresa.empresa.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST API para gestión de Empleados (MySQL/JPA)
 * Base URL: /api/empleados
 */
@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*") // Permitir CORS para pruebas
public class EmpleadoRestController {

    @Autowired
    private EmpleadoService empleadoService;

    /**
     * GET /api/empleados
     * Obtener todos los empleados
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Empleado> empleados = empleadoService.obtenerTodos();
            response.put("success", true);
            response.put("data", empleados);
            response.put("total", empleados.size());
            response.put("message", "Empleados obtenidos correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener empleados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/empleados/{id}
     * Obtener un empleado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Empleado> empleado = empleadoService.obtenerPorId(id);
            if (empleado.isPresent()) {
                response.put("success", true);
                response.put("data", empleado.get());
                response.put("message", "Empleado encontrado");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener empleado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/empleados
     * Crear un nuevo empleado
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Empleado empleado, BindingResult result) {
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
            // Validar email único
            if (empleadoService.existeEmail(empleado.getEmail())) {
                response.put("success", false);
                response.put("message", "El email ya está registrado");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            Empleado empleadoGuardado = empleadoService.guardar(empleado);
            response.put("success", true);
            response.put("data", empleadoGuardado);
            response.put("message", "Empleado creado exitosamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear empleado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/empleados/{id}
     * Actualizar un empleado existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Empleado empleado,
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
            Optional<Empleado> empleadoExistente = empleadoService.obtenerPorId(id);
            if (!empleadoExistente.isPresent()) {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validar email único (excepto el del empleado actual)
            Empleado empActual = empleadoExistente.get();
            if (!empActual.getEmail().equals(empleado.getEmail()) && 
                empleadoService.existeEmail(empleado.getEmail())) {
                response.put("success", false);
                response.put("message", "El email ya está registrado");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            empleado.setId(id);
            Empleado empleadoActualizado = empleadoService.guardar(empleado);
            response.put("success", true);
            response.put("data", empleadoActualizado);
            response.put("message", "Empleado actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar empleado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/empleados/{id}
     * Eliminar un empleado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Empleado> empleado = empleadoService.obtenerPorId(id);
            if (!empleado.isPresent()) {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            empleadoService.eliminar(id);
            response.put("success", true);
            response.put("message", "Empleado eliminado exitosamente");
            response.put("data", empleado.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar empleado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/empleados/buscar?termino={texto}
     * Buscar empleados por nombre o cargo
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String termino) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Empleado> empleados = empleadoService.buscarPorNombreOCargo(termino);
            response.put("success", true);
            response.put("data", empleados);
            response.put("total", empleados.size());
            response.put("message", "Búsqueda completada");
            response.put("termino", termino);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/empleados/email/{email}/existe
     * Verificar si un email ya está registrado
     */
    @GetMapping("/email/{email}/existe")
    public ResponseEntity<Map<String, Object>> verificarEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean existe = empleadoService.existeEmail(email);
            response.put("success", true);
            response.put("existe", existe);
            response.put("email", email);
            response.put("message", existe ? "El email ya está registrado" : "El email está disponible");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al verificar email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/empleados/cargo/{cargo}
     * Obtener empleados por cargo
     */
    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<Map<String, Object>> obtenerPorCargo(@PathVariable String cargo) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Empleado> empleados = empleadoService.buscarPorCargo(cargo);
            response.put("success", true);
            response.put("data", empleados);
            response.put("total", empleados.size());
            response.put("cargo", cargo);
            response.put("message", "Empleados por cargo obtenidos correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener empleados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PATCH /api/empleados/{id}/salario
     * Actualizar solo el salario de un empleado
     */
    @PatchMapping("/{id}/salario")
    public ResponseEntity<Map<String, Object>> actualizarSalario(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Empleado> empleadoOpt = empleadoService.obtenerPorId(id);
            if (!empleadoOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Double nuevoSalario = body.get("salario");
            if (nuevoSalario == null || nuevoSalario < 0) {
                response.put("success", false);
                response.put("message", "El salario debe ser un valor positivo");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Empleado empleado = empleadoOpt.get();
            empleado.setSalario(nuevoSalario);
            empleadoService.guardar(empleado);

            response.put("success", true);
            response.put("data", empleado);
            response.put("message", "Salario actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar salario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
