package com.alura.literatura.LiteraturaA;

import com.alura.literatura.LiteraturaA.model.Libro;
import com.alura.literatura.LiteraturaA.service.LibroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.alura.literatura.LiteraturaA.model.Autor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

@SpringBootApplication
public class DesafioApp {

    public static void main(String[] args) {
        SpringApplication.run(DesafioApp.class, args);
    }

    @Bean
    public CommandLineRunner run(LibroService libroService) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            int opcion = -1;

            while (opcion != 0) {
                System.out.println("\n==============================");
                System.out.println("      MENÚ LITERAlura");
                System.out.println("==============================");
                System.out.println("1 - Buscar libro por título");
                System.out.println("2 - Listar libros registrados");
                System.out.println("3 - Listar autores registrados");
                System.out.println("4 - Listar autores vivos en un determinado año");
                System.out.println("5 - Listar libros por idioma");
                System.out.println("6 - Ver top 10 libros más descargados");
                System.out.println("0 - Salir");
                System.out.print("Ingrese el número de la opción deseada: ");

                String entrada = scanner.nextLine().trim();
                try {
                    opcion = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un número válido (0-6).");
                    continue;
                }

                switch (opcion) {
                    /* 1 ── BUSCAR LIBRO ───────────────────────────────── */
                    case 1 -> {
                        System.out.print("Ingrese el título a buscar: ");
                        String titulo = scanner.nextLine().trim();

                        Libro libro = libroService.buscarLibroEnApiYGuardar(titulo);

                        if (libro == null) {
                            System.out.println("No se encontró ningún libro con ese título.");
                        } else {
                            System.out.println("\n----- LIBRO ENCONTRADO -----");
                            System.out.println("Título:    " + libro.getTitulo());
                            System.out.println("Autor:     " + (libro.getAutor() != null
                                    ? libro.getAutor().getNombre() : "─"));
                            System.out.println("Idioma:    " + libro.getIdioma());
                            System.out.println("Descargas: " + libro.getDescargas());
                        }
                    }

                    /* 2 ── LISTAR LIBROS REGISTRADOS ─────────────────── */
                    case 2 -> {
                        var libros = libroService.listarLibros();
                        System.out.println("\nTotal de libros registrados: " + libros.size());
                        libros.forEach(l -> System.out.println(
                                "• " + l.getTitulo()
                                        + " - " + (l.getAutor() != null ? l.getAutor().getNombre() : "─")
                                        + " (" + l.getIdioma() + ")"));
                    }

                    /* 3 ── LISTAR AUTORES REGISTRADOS ────────────────── */
                    case 3 -> {
                        List<Autor> autores = libroService.listarAutores();
                        System.out.println("\nTotal de autores registrados: " + autores.size());

                        for (Autor autor : autores) {
                            String nombre = autor.getNombre();
                            String nacimiento = (autor.getFechaNacimiento() != null)
                                    ? String.valueOf(autor.getFechaNacimiento().getYear())
                                    : "Desconocido";
                            String fallecimiento = (autor.getFechaMuerte() != null)
                                    ? String.valueOf(autor.getFechaMuerte().getYear())
                                    : "Desconocido";

                            String libros = autor.getLibros() != null && !autor.getLibros().isEmpty()
                                    ? autor.getLibros().stream()
                                    .map(Libro::getTitulo)
                                    .collect(Collectors.joining(", ", "[", "]"))
                                    : "[]";

                            System.out.println("\nAutor: " + nombre);
                            System.out.println("Fecha de nacimiento: " + nacimiento);
                            System.out.println("Fecha de fallecimiento: " + fallecimiento);
                            System.out.println("Libros: " + libros);
                        }
                    }

                    /* 4 ── AUTORES VIVOS EN UN AÑO ───────────────────── */
                    case 4 -> {
                        System.out.print("Ingrese el año (YYYY): ");
                        String texto = scanner.nextLine().trim();
                        try {
                            int anio = Integer.parseInt(texto);
                            var vivos = libroService.autoresVivosEn(anio);
                            System.out.println("\nAutores vivos en " + anio + ": " + vivos.size());
                            vivos.forEach(a -> System.out.println("• " + a.getNombre()));
                        } catch (NumberFormatException e) {
                            System.out.println("Año inválido.");
                        }
                    }

                    /* 5 ── LIBROS POR IDIOMA ─────────────────────────── */
                    case 5 -> {
                        System.out.print("Ingrese el código de idioma (ej. en, es, fr): ");
                        String lang = scanner.nextLine().trim();

                        var lista = libroService.listarLibrosPorIdioma(lang);
                        System.out.println("\nLibros en '" + lang + "': " + lista.size());

                        lista.forEach(l -> System.out.println(
                                "• " + l.getTitulo()
                                        + " - " + (l.getAutor() != null ? l.getAutor().getNombre() : "─")));
                    }

                    /* 6 ── TOP-10 DESCARGAS GUTENDEX ─────────────────── */
                    case 6 -> {
                        var top = libroService.mostrarTop10();
                        if (top.isEmpty()) {
                            System.out.println("No se pudo obtener el top 10 de Gutendex.");
                        } else {
                            System.out.println("\nTOP 10 libros más descargados:");
                            top.forEach(b -> System.out.println("• " + b.title()
                                    + " (" + b.downloadCount() + " descargas)"));
                        }
                    }

                    /* 0 ── SALIR ─────────────────────────────────────── */
                    case 0 -> {
                        System.out.println("¡Hasta luego!");
                        System.exit(0);
                    }


                    default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        };
    }
}
