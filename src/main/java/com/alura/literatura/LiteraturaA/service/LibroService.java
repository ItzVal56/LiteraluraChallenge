package com.alura.literatura.LiteraturaA.service;

import com.alura.literatura.LiteraturaA.model.Autor;
import com.alura.literatura.LiteraturaA.model.Libro;
import com.alura.literatura.LiteraturaA.repository.AutorRepository;
import com.alura.literatura.LiteraturaA.repository.LibroRepository;
import com.alura.literatura.LiteraturaA.service.service.dto.GutendexBook;      // ← paquete DTO correcto
import com.alura.literatura.LiteraturaA.service.service.dto.GutendexResponse; // ← paquete DTO correcto
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

    /* ------------ DEPENDENCIAS ------------ */
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final AutorService    autorService;
    private final WebClient       web;

    /* ------------ CONSTRUCTOR ------------- */
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
       1. Buscar libro en Gutendex y guardar si no existe
       ========================================================= */
    @Transactional
    public Libro buscarLibroEnApiYGuardar(String tituloBuscado) {

        /* 1-A.  ¿Ya existe tal cual lo escribió el usuario? */
        Optional<Libro> dup = libroRepository.findByTituloIgnoreCase(tituloBuscado.trim());
        if (dup.isPresent()) return dup.get();   // devuelve la entidad encontrada

        /* 1-B.  Llamar a Gutendex */
        GutendexResponse resp = web.get()
                .uri(uri -> uri.path("/books/")
                        .queryParam("search", tituloBuscado)
                        .build())
                .retrieve()
                .bodyToMono(GutendexResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if (resp == null || resp.results().isEmpty()) return null;   // no encontrado

        GutendexBook apiBook = resp.results().get(0);
        String tituloNormalizado = apiBook.title().replaceAll("\\s+", " ").trim();

        /* 1-C.  ¿Ya existe con el título oficial devuelto? */
        Optional<Libro> existente = libroRepository.findByTituloIgnoreCase(tituloNormalizado);
        if (existente.isPresent()) return existente.get();           // ← CAMBIO: devuelve entidad

        /* ---------- Crear Autor si es necesario ---------- */
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

        /* ---------- Crear y guardar Libro ---------- */
        Libro libro = new Libro();
        libro.setTitulo(tituloNormalizado);
        libro.setDescargas(apiBook.downloadCount());
        if (apiBook.languages() != null && !apiBook.languages().isEmpty())
            libro.setIdioma(apiBook.languages().get(0));
        libro.setAutor(autorEntidad);

        return libroRepository.save(libro);
    }

    /* Alias para el menú */
    public Libro buscarYGuardar(String titulo) {
        return buscarLibroEnApiYGuardar(titulo);
    }

    /* =========================================================
       2.  Listar y contar libros
       ========================================================= */
    public List<Libro> listarLibros()        { return libroRepository.findAll(); }
    public List<Libro> listarTodos()         { return listarLibros(); }
    public long contarLibros()               { return libroRepository.count();  }

    /* =========================================================
       3.  Listar / contar por idioma
       ========================================================= */
    public List<Libro> listarLibrosPorIdioma(String idioma) {
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
       4.  Operaciones con autores (delegadas)
       ========================================================= */
    public List<Autor> listarAutores()          { return autorService.listarAutores(); }
    public long        contarAutores()          { return autorService.contarAutores(); }
    public List<Autor> autoresVivosEn(int y)    { return autorService.listarAutoresVivosEn(y); }
    public long        contarAutoresVivosEn(int y){ return autorService.contarAutoresVivosEn(y); }

    /* =========================================================
       5.  Top-10 más descargados (Gutendex)
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
    public List<GutendexBook> mostrarTop10() { return top10Descargados(); }
}
