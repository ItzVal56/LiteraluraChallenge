package com.alura.literatura.LiteraturaA.service.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GutendexBook(
        String title,
        @JsonProperty("download_count") Integer downloadCount,
        List<String> languages,
        List<GutendexAuthor> authors) {}
