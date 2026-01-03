package com.prueba.msvc.items.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.prueba.msvc.items.models.Item;
import com.prueba.msvc.items.models.Product;
import com.prueba.msvc.items.services.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RefreshScope
@RestController
public class ItemController {

    private final ItemService itemService;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${configuracion.texto}")
    private String text;

    @Autowired
    private Environment env;

    public ItemController(ItemService itemService, CircuitBreakerFactory circuitBreakerFactory) {
        this.itemService = itemService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping
    public List<Item> list(@RequestParam(name = "name", required = false) String nombre, @RequestHeader("token-request") String tokenRequest) {
        System.out.println("nombre: " + nombre);
        System.out.println("token-request: " + tokenRequest);
        return itemService.findAll();
    }

    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String port) {
        Map<String, String> json = new HashMap<>();
        json.put("text", text);
        json.put("port", port);
        if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
            json.put("author.name", env.getProperty("configuracion.autor.nombre"));
            json.put("author.email", env.getProperty("configuracion.autor.email"));
        }
        return ResponseEntity.ok(json);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Item> details(@PathVariable Long id) {
        try {
            Optional<Item> itemOptional = circuitBreakerFactory.create("items").run(() -> itemService.findById(id),
                    e -> {
                        Product product = new Product();
                        product.setId(1L);
                        product.setCreateAt(LocalDate.now());
                        product.setName("Camara Sony");
                        product.setPrice(500.00);
                        Item item = new Item(product, 5);
                        return Optional.of(item);
                    });
            return ResponseEntity.ok(itemOptional.get());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CircuitBreaker(name = "items", fallbackMethod = "fallbackDetails2")
    @GetMapping("/details/{id}")
    public ResponseEntity<Item> details2(@PathVariable Long id) {
        try {
            Optional<Item> itemOptional = itemService.findById(id);
            return ResponseEntity.ok(itemOptional.get());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Item> fallbackDetails2(Long id, Throwable e) {
        Product product = new Product();
        product.setId(1L);
        product.setCreateAt(LocalDate.now());
        product.setName("Camara Sony");
        product.setPrice(500.00);
        Item item = new Item(product, 5);
        return ResponseEntity.ok(item);
    }
    
    
}
