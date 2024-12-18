package com.example.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

}
class Coffee {
    private final String id;
    private String name;

    Coffee(String id, String name) {
        this.id = id;
        this.name = name;
    }
    Coffee(String name) {
        this(UUID.randomUUID().toString(), name);
    }
    Coffee() {
        this("default name");
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
    private final List<Coffee> coffees = new ArrayList<>();
    public RestApiDemoController() {
        coffees.addAll(List.of(
                new Coffee("Café Cereal"),
                new Coffee("Café Ganador"),
                new Coffee("Café Laredo"),
                new Coffee("Café Três Pontus")
        ));
    }
    @GetMapping
    Iterable<Coffee> getCoffees() {
        return coffees;
    }

    @GetMapping("/{id}")
    Optional<Coffee> getCoffeeById(@PathVariable String id) {
        for (Coffee c: coffees) {
            if (c.getId().equals(id)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
    @PostMapping
    Coffee postCoffee(@RequestBody Coffee coffee) {
        coffees.add(coffee);
        return coffee;//automatically unmarshalled by Spring Boot to JSON by default
    }
    @PutMapping("/{id}")
    ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody String name) {
        int coffeeIndex = -1;
        for (Coffee c: coffees) {
            if (c.getId().equals(id)) {
                coffeeIndex = coffees.indexOf(c);
                coffees.get(coffeeIndex).setName(name);
            }
        }
        return (coffeeIndex == -1) ?
                new ResponseEntity<>(postCoffee(new Coffee("default name")), HttpStatus.CREATED) :
                new ResponseEntity<>(coffees.get(coffeeIndex), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    boolean deleteCoffee(@PathVariable String id) {
        return coffees.removeIf(c -> c.getId().equals(id));
    }
}
