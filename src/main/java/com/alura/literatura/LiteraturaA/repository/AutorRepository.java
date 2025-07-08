package com.alura.literatura.LiteraturaA.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import com.alura.literatura.LiteraturaA.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreIgnoreCase(String nombre);

    @Query(""" 
           SELECT a 
           FROM Autor a 
           LEFT JOIN FETCH a.libros 
           """)
    List<Autor> findAllConLibros();
}