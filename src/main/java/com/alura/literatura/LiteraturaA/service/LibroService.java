package com.alura.literatura.LiteraturaA.service;

import com.alura.literatura.LiteraturaA.model.Autor;
import com.alura.literatura.LiteraturaA.model.Libro;
import com.alura.literatura.LiteraturaA.repository.AutorRepository;
import com.alura.literatura.LiteraturaA.repository.LibroRepository;
import com.alura.literatura.LiteraturaA.service.service.dto.GutendexBook;      // ← OJO: paquete correcto
import com.alura.literatura.LiteraturaA.service.service.dto.GutendexResponse; // ← OJO: paquete correcto
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroService {

    /* --------------- DEPENDENCIAS ---------------- */
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final AutorService   autorService;       // usamos métodos de autores
    private final WebClient web;

    /* --------------- CONSTRUCTOR ----------------- */
    public LibroService(LibroRepository libroRepository,
                        AutorRepository autorRepository,
                        AutorService autorService) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.autorService    = autorService;
        this.web = WebClient.builder()
                .baseUrl("https://gutendex.com")
                .build();
    }

    /* =========================================================
       1. Buscar un libro en Gutendex y guardarlo si no existe
       ========================================================= */
    @Transactional
    public Libro buscarLibroEnApiYGuardar(String tituloBuscado) {

        // ¿Ya existe en BD?
        Optional<Libro> dup = libroRepository.findByTituloIgnoreCase(tituloBuscado.trim());
        if (dup.isPresent()) return dup.get();

        // Llamar a Gutendex
        GutendexResponse resp = web.get()
                .uri(uri -> uri.path("/books/")
                        .queryParam("search", tituloBuscado)
                        .build())
                .retrieve()
                .bodyToMono(GutendexResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if (resp == null || resp.results().isEmpty()) return null;      // no encontrado

        GutendexBook apiBook = resp.results().get(0);
        String tituloNormalizado = apiBook.title().replaceAll("\\s+", " ").trim();

        // ¿Duplicado con el título normalizado?
        if (libroRepository.findByTituloIgnoreCase(tituloNormalizado).isPresent()) return null;

        /* ---------- Autor ---------- */
        Autor autorEntidad = null;
        if (apiBook.authors() != null && !apiBook.authors().isEmpty()) {
            var apiAutor = apiBook.authors().get(0);
            autorEntidad = autorRepository.findByNombreIgnoreCase(apiAutor.name())
                    .orElseGet(() -> {
                        Autor nuevo = new Autor();
                        nuevo.setNombre(apiAutor.name());
                        if (apiAutor.birthYear() != null)
                            nuevo.setFechaNacimiento(LocalDate.of(apiAutor.birthYear(), 1, 1));
                        if (apiAutor.deathYear() != null)
                            nuevo.setFechaMuerte(LocalDate.of(apiAutor.deathYear(), 1, 1));
                        return autorRepository.save(nuevo);
                    });
        }

        /* ---------- Libro ---------- */
        Libro libro = new Libro();
        libro.setTitulo(tituloNormalizado);
        libro.setDescargas(apiBook.downloadCount());
        if (apiBook.languages() != null && !apiBook.languages().isEmpty())
            libro.setIdioma(apiBook.languages().get(0));
        libro.setAutor(autorEntidad);

        return libroRepository.save(libro);
    }

    /* Alias para el menú (por compatibilidad con MainConsola) */
    public Libro buscarYGuardar(String titulo) {
        return buscarLibroEnApiYGuardar(titulo);
    }

    /* =========================================================
       2.  Listar y contar libros
       ========================================================= */
    public List<Libro> listarLibros()               { return libroRepository.findAll(); }
    public List<Libro> listarTodos()                { return listarLibros(); }          // alias
    public long contarLibros()                      { return libroRepository.count();  }

    /* =========================================================
       3.  Listar / contar por idioma
       ========================================================= */
    public List<Libro> listarLibrosPorIdioma(String idioma) {       // alias para MainConsola
        return listarPorIdioma(idioma);
    }

    public List<Libro> listarPorIdioma(String idioma) {
        return libroRepository.findAll().stream()
                .filter(l -> l.getIdioma() != null && l.getIdioma().equalsIgnoreCase(idioma))
                .collect(Collectors.toList());
    }

    public long contarPorIdioma(String idioma) {
        return listarPorIdioma(idioma).size();
    }

    /* =========================================================
       4.  Métodos que delegan a AutorService
       ========================================================= */
    public List<Autor> listarAutores()                 { return autorService.listarAutores(); }
    public long contarAutores()                        { return autorService.contarAutores();  }

    public List<Autor> autoresVivosEn(int anio)        { return autorService.listarAutoresVivosEn(anio); }
    public long contarAutoresVivosEn(int anio)         { return autorService.contarAutoresVivosEn(anio); }

    /* =========================================================
       5.  Top-10 más descargados desde Gutendex (sin persistir)
       ========================================================= */
    public List<GutendexBook> top10Descargados() {
        GutendexResponse resp = web.get()
                .uri(uri -> uri.path("/books/")
                        .queryParam("sort", "download_count")
                        .queryParam("page", "1")
                        .build())
                .retrieve()
                .bodyToMono(GutendexResponse.class)
                .block();

        if (resp == null) return List.of();
        return resp.results().stream().limit(10).collect(Collectors.toList());
    }

    /* Método de conveniencia para MainConsola */
    public List<GutendexBook> mostrarTop10() {
        return top10Descargados();
    }
}
