package com.example.carmanagement.controller;

import com.example.carmanagement.model.Car;
import com.example.carmanagement.model.FuelStats;
import com.example.carmanagement.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Map<String, Object> carData) {
        String brand = (String) carData.get("brand");
        String model = (String) carData.get("model");
        int year = (Integer) carData.get("year");

        Car car = carService.createCar(brand, model, year);
        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @PostMapping("/{id}/fuel")
    public ResponseEntity<String> addFuelEntry(
            @PathVariable Long id,
            @RequestBody Map<String, Object> fuelData) {

        double liters = ((Number) fuelData.get("liters")).doubleValue();
        double price = ((Number) fuelData.get("price")).doubleValue();
        int odometer = (Integer) fuelData.get("odometer");

        boolean success = carService.addFuelEntry(id, liters, price, odometer);

        if (success) {
            return ResponseEntity.ok("Fuel entry added successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Car not found with id: " + id);
        }
    }

    @GetMapping("/{id}/fuel/stats")
    public ResponseEntity<FuelStats> getFuelStats(@PathVariable Long id) {
        return carService.getFuelStats(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}