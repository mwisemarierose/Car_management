package com.example.carmanagement.service;

import com.example.carmanagement.model.Car;
import com.example.carmanagement.model.FuelEntry;
import com.example.carmanagement.model.FuelStats;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarService {
    private final Map<Long, Car> cars = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Car createCar(String brand, String model, int year) {
        Long id = idCounter.getAndIncrement();
        Car car = new Car(id, brand, model, year);
        cars.put(id, car);
        return car;
    }

    public List<Car> getAllCars() {
        return new ArrayList<>(cars.values());
    }

    public Optional<Car> getCarById(Long id) {
        return Optional.ofNullable(cars.get(id));
    }

    public boolean addFuelEntry(Long carId, double liters, double price, int odometer) {
        Car car = cars.get(carId);
        if (car == null) {
            return false;
        }
        FuelEntry entry = new FuelEntry(liters, price, odometer);
        car.addFuelEntry(entry);
        return true;
    }

    public Optional<FuelStats> getFuelStats(Long carId) {
        Car car = cars.get(carId);
        if (car == null) {
            return Optional.empty();
        }

        List<FuelEntry> entries = car.getFuelEntries();
        if (entries.isEmpty()) {
            return Optional.of(new FuelStats(0, 0, 0));
        }

        double totalFuel = 0;
        double totalCost = 0;
        for (FuelEntry entry : entries) {
            totalFuel += entry.getLiters();
            totalCost += entry.getPrice();
        }

        double avgConsumption = 0;
        if (entries.size() >= 2) {
            entries.sort(Comparator.comparingInt(FuelEntry::getOdometer));
            int firstOdometer = entries.get(0).getOdometer();
            int lastOdometer = entries.get(entries.size() - 1).getOdometer();
            int distance = lastOdometer - firstOdometer;

            if (distance > 0) {
                avgConsumption = (totalFuel / distance) * 100;
            }
        }

        return Optional.of(new FuelStats(totalFuel, totalCost, avgConsumption));
    }
}