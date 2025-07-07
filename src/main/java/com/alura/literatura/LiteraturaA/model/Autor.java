package com.alura.literatura.LiteraturaA.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Autor {

    /* ---------- CAMPOS ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private LocalDate fechaNacimiento;
    private LocalDate fechaMuerte;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Libro> libros;

    /* ---------- GETTERS ---------- */
    public Long getId()                 { return id; }
    public String getNombre()           { return nombre; }
    public LocalDate getFechaNacimiento(){ return fechaNacimiento; }
    public LocalDate getFechaMuerte()   { return fechaMuerte; }
    public List<Libro> getLibros()      { return libros; }

    /* ---------- SETTERS ---------- */
    public void setId(Long id)                             { this.id = id; }
    public void setNombre(String nombre)                   { this.nombre = nombre; }
    public void setFechaNacimiento(LocalDate nacimiento)   { this.fechaNacimiento = nacimiento; }
    public void setFechaMuerte(LocalDate muerte)           { this.fechaMuerte = muerte; }
    public void setLibros(List<Libro> libros)              { this.libros = libros; }
}
