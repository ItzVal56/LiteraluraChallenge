package com.alura.literatura.LiteraturaA.service;

import com.alura.literatura.LiteraturaA.model.Autor;
import com.alura.literatura.LiteraturaA.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }

    public long contarAutores() {
        return autorRepository.count();
    }

    public List<Autor> listarAutoresVivosEn(int anio) {
        return autorRepository.findAll().stream()
                .filter(autor -> autor.getFechaNacimiento() != null && autor.getFechaNacimiento().getYear() <= anio)
                .filter(autor -> autor.getFechaMuerte() == null || autor.getFechaMuerte().getYear() > anio)
                .collect(Collectors.toList());
    }

    public long contarAutoresVivosEn(int anio) {
        return listarAutoresVivosEn(anio).size();
    }
}
