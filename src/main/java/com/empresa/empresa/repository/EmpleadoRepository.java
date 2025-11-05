package com.empresa.empresa.repository;

import com.empresa.empresa.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    // Verificar si existe un empleado con el email dado
    boolean existsByEmail(String email);
    
    // Buscar empleados por nombre (contiene)
    List<Empleado> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar empleados por cargo (contiene)
    List<Empleado> findByCargoContainingIgnoreCase(String cargo);
    
    // Buscar empleados por nombre o cargo
    @Query("SELECT e FROM Empleado e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(e.cargo) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Empleado> buscarPorNombreOCargo(@Param("termino") String termino);
}
