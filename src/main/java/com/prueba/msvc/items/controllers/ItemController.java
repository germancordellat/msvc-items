package com.prueba.msvc.items.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.prueba.msvc.items.models.Item;
import com.prueba.msvc.items.services.ItemService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> list() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> details(@PathVariable Long id) {
        try {
            Item item = itemService.findById(id).orElseThrow();
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
}
