package com.empresa.empresa.entity;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class Tarea {
    
    private String id;
    
    @NotBlank(message = "El título de la tarea es obligatorio")
    private String titulo;
    
    private String descripcion;
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;
    
    private boolean completada = false;
    
    // Constructor vacío
    public Tarea() {
    }
    
    // Constructor con parámetros
    public Tarea(String titulo, String descripcion, LocalDate fechaVencimiento) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaVencimiento = fechaVencimiento;
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public boolean isCompletada() {
        return completada;
    }
    
    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
}