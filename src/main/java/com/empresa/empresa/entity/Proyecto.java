package com.empresa.empresa.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "proyectos")
public class Proyecto {
    
    @Id
    private String id;
    
    @NotBlank(message = "El nombre del proyecto es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    private Long empleadoId;
    
    private List<Tarea> tareas = new ArrayList<>();
    
    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaEstimadaFin;
    
    @NotBlank(message = "El estado del proyecto es obligatorio")
    private String estado;
    
    private List<Empleado> empleados = new ArrayList<>();
    
    // Constructor vacío
    public Proyecto() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "Pendiente";
    }
    
    // Constructor con parámetros
    public Proyecto(String nombre, String descripcion, Long empleadoId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.empleadoId = empleadoId;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "Pendiente";
    }
    
    // Constructor completo
    public Proyecto(String nombre, String descripcion, Long empleadoId, LocalDateTime fechaEstimadaFin, String estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.empleadoId = empleadoId;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaEstimadaFin = fechaEstimadaFin;
        this.estado = estado;
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Long getEmpleadoId() {
        return empleadoId;
    }
    
    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }
    
    public List<Tarea> getTareas() {
        return tareas;
    }
    
    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaEstimadaFin() {
        return fechaEstimadaFin;
    }
    
    public void setFechaEstimadaFin(LocalDateTime fechaEstimadaFin) {
        this.fechaEstimadaFin = fechaEstimadaFin;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public List<Empleado> getEmpleados() {
        return empleados;
    }
    
    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }
    
    // Método para agregar una tarea al proyecto
    public void agregarTarea(Tarea tarea) {
        this.tareas.add(tarea);
    }
    
    // Método para calcular el porcentaje de progreso
    public int calcularPorcentajeProgreso() {
        if (tareas.isEmpty()) {
            return 0;
        }
        
        long tareasCompletadas = tareas.stream().filter(Tarea::isCompletada).count();
        return (int) ((tareasCompletadas * 100) / tareas.size());
    }
    
    // Getter para el porcentaje completado (usado en la plantilla)
    public int getPorcentajeCompletado() {
        return calcularPorcentajeProgreso();
    }
    
    // Getter para el número de tareas completadas (usado en la plantilla)
    public long getTareasCompletadas() {
        if (tareas == null || tareas.isEmpty()) {
            return 0;
        }
        return tareas.stream().filter(Tarea::isCompletada).count();
    }
}
