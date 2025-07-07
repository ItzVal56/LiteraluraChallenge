package com.alura.literatura.LiteraturaA.repository;

import com.alura.literatura.LiteraturaA.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.alura.literatura.LiteraturaA.model.Libro;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloIgnoreCase(String titulo);
    List<Libro> findByIdiomaIgnoreCase(String idioma);
}