package com.empresa.empresa.service;

import com.empresa.empresa.entity.Proyecto;
import com.empresa.empresa.entity.Tarea;
import com.empresa.empresa.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProyectoService {
    
    @Autowired
    private ProyectoRepository proyectoRepository;
    
    // Obtener todos los proyectos
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }
    
    // Obtener un proyecto por su ID
    public Optional<Proyecto> obtenerPorId(String id) {
        return proyectoRepository.findById(id);
    }
    
    // Guardar un proyecto
    public Proyecto guardar(Proyecto proyecto) {
        // Normalizar id vacía ("") a null para que MongoDB genere un ObjectId
        if (proyecto.getId() != null && proyecto.getId().trim().isEmpty()) {
            proyecto.setId(null);
        }
        return proyectoRepository.save(proyecto);
    }
    
    // Eliminar un proyecto
    public void eliminar(String id) {
        proyectoRepository.deleteById(id);
    }
    
    // Buscar proyectos por nombre
    public List<Proyecto> buscarPorNombre(String nombre) {
        return proyectoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    // Buscar proyectos por empleadoId
    public List<Proyecto> buscarPorEmpleadoId(Long empleadoId) {
        // Buscar en ambos lugares: campo directo empleadoId y dentro del array empleados
        List<Proyecto> proyectosDirectos = proyectoRepository.findByEmpleadoId(empleadoId);
        List<Proyecto> proyectosEnArray = proyectoRepository.findByEmpleadosId(empleadoId);
        
        // Combinar resultados sin duplicados
        proyectosDirectos.addAll(proyectosEnArray.stream()
            .filter(p -> !proyectosDirectos.contains(p))
            .toList());
        
        return proyectosDirectos;
    }
    
    // Agregar una tarea a un proyecto
    public Proyecto agregarTarea(String proyectoId, Tarea tarea) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (proyectoOpt.isPresent()) {
            Proyecto proyecto = proyectoOpt.get();
            // Generar id para la tarea si no tiene (para poder referenciarla posteriormente)
            if (tarea.getId() == null || tarea.getId().trim().isEmpty()) {
                tarea.setId(UUID.randomUUID().toString());
            }
            proyecto.agregarTarea(tarea);
            return proyectoRepository.save(proyecto);
        }
        return null;
    }
}
