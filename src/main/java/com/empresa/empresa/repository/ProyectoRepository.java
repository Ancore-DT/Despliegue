package com.empresa.empresa.repository;

import com.empresa.empresa.entity.Proyecto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends MongoRepository<Proyecto, String> {
    
    // Buscar proyectos por nombre (contiene)
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar proyectos por empleadoId (campo directo)
    List<Proyecto> findByEmpleadoId(Long empleadoId);
    
    // Buscar proyectos donde el empleado está en el array de empleados
    @Query("{ 'empleados._id': ?0 }")
    List<Proyecto> findByEmpleadosId(Long empleadoId);
}
