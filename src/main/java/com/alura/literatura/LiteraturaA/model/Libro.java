package com.alura.literatura.LiteraturaA.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libro",
        uniqueConstraints = @UniqueConstraint(columnNames = "titulo"))
public class Libro {

    /* ---------- CAMPOS ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String idioma;
    private Integer descargas;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    /* ---------- GETTERS ---------- */
    public Long getId()           { return id; }
    public String getTitulo()     { return titulo; }
    public String getIdioma()     { return idioma; }
    public Integer getDescargas() { return descargas; }
    public Autor getAutor()       { return autor; }

    /* ---------- SETTERS ---------- */
    public void setId(Long id)               { this.id = id; }
    public void setTitulo(String titulo)     { this.titulo = titulo; }
    public void setIdioma(String idioma)     { this.idioma = idioma; }
    public void setDescargas(Integer d)      { this.descargas = d; }
    public void setAutor(Autor autor)        { this.autor = autor; }
}
