package com.prueba.msvc.items.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.prueba.msvc.items.models.Item;

@Service
public class ItemServiceWebClient implements ItemService {

    private final WebClient.Builder client;

    public ItemServiceWebClient(WebClient.Builder client) {
        this.client = client;
    }

    @Override
    public List<Item> findAll() {
        return client.build().get()
                .uri("http://msvc-products")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Item.class)
                .collectList()
                .block();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        return client.build().get()
                .uri("http://msvc-products/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Item.class)
                .blockOptional();
    }

}
