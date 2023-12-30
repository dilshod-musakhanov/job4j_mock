package ru.checkdev.notification.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.CategoryDTO;

import java.util.List;

@Service
@Slf4j
public class TgCategoryCallWebClient {
    private WebClient webClient;

    public TgCategoryCallWebClient(@Value("${server.cat}") String urlCat) {
        this.webClient = WebClient.create(urlCat);
    }

    public Mono<List<CategoryDTO>> doGetCategories(String url) {
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(CategoryDTO.class)
                .collectList()
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }
}
