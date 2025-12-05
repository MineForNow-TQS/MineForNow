package tqs.backend.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.backend.model.Car;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "http://localhost:3000")
public class CarController {

    @GetMapping
    public List<Car> getAllCars() {
        return Arrays.asList(
            new Car(1L, "Fiat", "500", "Lisboa", 35.0, "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2"),
            new Car(2L, "Tesla", "Model 3", "Porto", 85.0, "https://images.unsplash.com/photo-1560958089-b8a1929cea89"),
            new Car(3L, "Renault", "Clio", "Faro", 25.0, "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb")
        );
    }
}