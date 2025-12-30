package com.prueba.msvc.items.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.prueba.msvc.items.clients.ProductFeignClient;
import com.prueba.msvc.items.models.Item;

@Service
public class ItemServiceFeign implements ItemService {

    private final ProductFeignClient productFeignClient;

    public ItemServiceFeign(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    @Override
    public List<Item> findAll() {
        return productFeignClient.findAll()
                .stream()
                .map(product -> new Item(product, 1))
                .toList();
    }

    @Override
    public Optional<Item> findById(Long id) {
        var product = productFeignClient.details(id);
        return Optional.of(new Item(product, 1));
    }

}
