package com.alura.literatura.LiteraturaA.service;

import com.alura.literatura.LiteraturaA.service.service.dto.GutendexBook;
import com.alura.literatura.LiteraturaA.service.service.dto.GutendexResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GutendexService {

    private final WebClient web = WebClient.builder()
            .baseUrl("https://gutendex.com")
            .build();

    public GutendexBook buscarLibroPorTitulo(String titulo) {
        GutendexResponse resp = web.get()
                .uri(uri -> uri.path("/books/").queryParam("search", titulo).build())
                .retrieve()
                .bodyToMono(GutendexResponse.class)
                .block();

        if (resp == null || resp.results().isEmpty()) return null;
        return resp.results().get(0);   // Java 17: usamos get(0)
    }
}
