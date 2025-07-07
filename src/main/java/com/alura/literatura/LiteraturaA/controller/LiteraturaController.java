package com.alura.literatura.LiteraturaA.controller;

import com.alura.literatura.LiteraturaA.model.Autor;
import com.alura.literatura.LiteraturaA.model.Libro;
import com.alura.literatura.LiteraturaA.repository.AutorRepository;
import com.alura.literatura.LiteraturaA.repository.LibroRepository;
import com.alura.literatura.LiteraturaA.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class LiteraturaController {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroService libroService;      //inyectamos la lógica de Gutendex

    @GetMapping
    public List<Libro> obtenerTodos() {
        return libroRepository.findAll();
    }

    @PostMapping
    public Libro guardarLibro(@RequestBody Libro libro) {
        return libroRepository.save(libro);
    }

    @GetMapping("/{titulo}")
    public Libro buscarPorTitulo(@PathVariable String titulo) {
        return libroRepository.findByTituloIgnoreCase(titulo).orElse(null);
    }

    @GetMapping("/autor/{nombre}")
    public Autor buscarAutorPorNombre(@PathVariable String nombre) {
        return autorRepository.findByNombreIgnoreCase(nombre).orElse(null);
    }

    @PostMapping("/buscar")
    public ResponseEntity<String> buscarYGuardar(@RequestParam String titulo) {
        var libro = libroService.buscarLibroEnApiYGuardar(titulo);

        if (libro == null) {
            return ResponseEntity
                    .status(404)
                    .body("No se encontró el libro o ya existía.");
        }

        String msg = "\n----- LIBRO -----\n" +
                "Título: " + libro.getTitulo() + "\n" +
                "Autor: "  + (libro.getAutor() != null ? libro.getAutor().getNombre() : "─") + "\n" +
                "Idioma: " + libro.getIdioma() + "\n" +
                "Número de descargas: " + libro.getDescargas() + "\n";

        return ResponseEntity.ok(msg);
    }


    @GetMapping("/idioma")
    public List<Libro> obtenerLibrosPorIdioma(@RequestParam String lang) {
        return libroRepository.findByIdiomaIgnoreCase(lang);
    }

    @DeleteMapping("/{id}")
    public void eliminarLibro(@PathVariable Long id) {
        libroRepository.deleteById(id);
    }
}
