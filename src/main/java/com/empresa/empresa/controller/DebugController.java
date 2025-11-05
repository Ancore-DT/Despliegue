package com.empresa.empresa.controller;

import com.empresa.empresa.repository.ProyectoRepository;
import com.empresa.empresa.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> r = new HashMap<>();
        r.put("ok", true);
        r.put("env", "debug");
        return ResponseEntity.ok(r);
    }

    @GetMapping("/proyectos/count")
    public ResponseEntity<Map<String, Object>> countProyectos() {
        Map<String, Object> r = new HashMap<>();
        long count = proyectoRepository.count();
        r.put("count", count);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/proyectos/{id}")
    public ResponseEntity<Object> getProyecto(@PathVariable String id) {
        return proyectoRepository.findById(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Proyecto no encontrado")));
    }

    @DeleteMapping("/proyectos/{id}")
    public ResponseEntity<Map<String, Object>> deleteProyecto(@PathVariable String id) {
        Map<String, Object> r = new HashMap<>();
        try {
            proyectoService.eliminar(id);
            r.put("success", true);
            r.put("mensaje", "Eliminado (debug)");
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            r.put("success", false);
            r.put("mensaje", e.getMessage());
            return ResponseEntity.status(500).body(r);
        }
    }
}
