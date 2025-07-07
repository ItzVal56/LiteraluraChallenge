package com.alura.literatura.LiteraturaA;

import com.alura.literatura.LiteraturaA.model.Libro;
import com.alura.literatura.LiteraturaA.service.LibroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
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
                System.out.println("\nElija la opción a través de su número:");
                System.out.println("1 - Buscar libro por título");
                System.out.println("2 - Listar libros registrados");
                System.out.println("3 - Listar autores registrados");
                System.out.println("4 - Listar autores vivos en un determinado año");
                System.out.println("5 - Listar libros por idioma");
                System.out.println("6 - Ver top 10 libros más descargados");
                System.out.println("0 - Salir");

                try {
                    opcion = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingresa un número válido.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> {                                           // buscar libro
                        System.out.print("Ingrese el nombre del libro: ");
                        String titulo = scanner.nextLine();
                        Libro libro = libroService.buscarLibroEnApiYGuardar(titulo);
                        if (libro != null) {
                            System.out.println("\n----- LIBRO ENCONTRADO -----");
                            System.out.println("Título:   " + libro.getTitulo());
                            System.out.println("Autor:    " + (libro.getAutor()!=null?libro.getAutor().getNombre():"─"));
                            System.out.println("Idioma:   " + libro.getIdioma());
                            System.out.println("Descargas:" + libro.getDescargas());
                        } else {
                            System.out.println("⚠️  No se encontró el libro o ya estaba registrado.");
                        }
                    }

                    case 2 -> {                                           // listar libros
                        List<Libro> libros = libroService.listarLibros();
                        System.out.println("\nSe han registrado " + libros.size() + " libros:");
                        libros.forEach(l -> System.out.println("• " + l.getTitulo() + " (" + l.getIdioma() + ")"));
                    }

                    case 3 -> {                                           // listar autores
                        var autores = libroService.listarAutores();
                        System.out.println("\nSe han registrado " + autores.size() + " autores:");
                        autores.forEach(a -> System.out.println("• " + a.getNombre()));
                    }

                    case 4 -> {                                           // autores vivos en año
                        System.out.print("Ingrese el año (YYYY): ");
                        String texto = scanner.nextLine();
                        try {
                            int anio = Integer.parseInt(texto);
                            var vivos = libroService.autoresVivosEn(anio);
                            System.out.println("\nAutores vivos en " + anio + ": " + vivos.size());
                            vivos.forEach(a -> System.out.println("• " + a.getNombre()));
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️  Año inválido.");
                        }
                    }

                    case 5 -> {                                           // libros por idioma
                        System.out.print("Ingrese código de idioma (ej: en, es, fr): ");
                        String lang = scanner.nextLine().trim();
                        var porIdioma = libroService.listarLibrosPorIdioma(lang);
                        System.out.println("\nLibros en '" + lang + "': " + porIdioma.size());
                        porIdioma.forEach(l -> System.out.println("• " + l.getTitulo()));
                    }

                    case 6 -> {                                           // top-10 Gutendex
                        var top = libroService.mostrarTop10();
                        System.out.println("\nTOP 10 libros más descargados en Gutendex:");
                        top.forEach(b -> System.out.println(
                                "• " + b.title() + " (" + b.downloadCount() + " descargas)"));
                    }

                    case 0 -> System.out.println("Saliendo…");
                    default -> { /* si no coincide con ninguno, no hacemos nada */ }
                }
            }
        };
    }
}
