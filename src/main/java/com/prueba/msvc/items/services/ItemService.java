package com.prueba.msvc.items.services;

import java.util.List;
import java.util.Optional;

import com.prueba.libs.mscv.commons.entities.Product;
import com.prueba.msvc.items.models.Item;

public interface ItemService {

    List<Item> findAll();

    Optional<Item> findById(Long id);

    Product save(Product product);

    Product update(Product product, Long id);
    
    void deleteById(Long id);
}
