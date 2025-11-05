package com.empresa.empresa.service;

import com.empresa.empresa.entity.Empleado;
import com.empresa.empresa.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {
    
    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    // Obtener todos los empleados
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }
    
    // Obtener un empleado por su ID
    public Optional<Empleado> obtenerPorId(Long id) {
        return empleadoRepository.findById(id);
    }
    
    // Guardar un empleado
    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }
    
    // Eliminar un empleado
    public void eliminar(Long id) {
        empleadoRepository.deleteById(id);
    }
    
    // Verificar si existe un empleado con el email dado
    public boolean existeEmail(String email) {
        return empleadoRepository.existsByEmail(email);
    }
    
    // Buscar empleados por nombre
    public List<Empleado> buscarPorNombre(String nombre) {
        return empleadoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    // Buscar empleados por cargo
    public List<Empleado> buscarPorCargo(String cargo) {
        return empleadoRepository.findByCargoContainingIgnoreCase(cargo);
    }
    
    // Buscar empleados por nombre o cargo
    public List<Empleado> buscarPorNombreOCargo(String termino) {
        return empleadoRepository.buscarPorNombreOCargo(termino);
    }
}
