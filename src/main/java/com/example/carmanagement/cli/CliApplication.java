package com.example.carmanagement.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class CliApplication {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            String command = args[0];
            switch (command) {
                case "create-car":
                    handleCreateCar(args);
                    break;
                case "add-fuel":
                    handleAddFuel(args);
                    break;
                case "fuel-stats":
                    handleFuelStats(args);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    printUsage();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleCreateCar(String[] args) throws Exception {
        String brand = getArgValue(args, "--brand");
        String model = getArgValue(args, "--model");
        String yearStr = getArgValue(args, "--year");

        if (brand == null || model == null || yearStr == null) {
            System.out.println("Usage: create-car --brand <brand> --model <model> --year <year>");
            return;
        }

        int year = Integer.parseInt(yearStr);

        Map<String, Object> carData = new HashMap<>();
        carData.put("brand", brand);
        carData.put("model", model);
        carData.put("year", year);

        String jsonBody = gson.toJson(carData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/cars"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            JsonObject car = gson.fromJson(response.body(), JsonObject.class);
            System.out.println("Car created successfully!");
            System.out.println("ID: " + car.get("id").getAsLong());
            System.out.println("Brand: " + car.get("brand").getAsString());
            System.out.println("Model: " + car.get("model").getAsString());
            System.out.println("Year: " + car.get("year").getAsInt());
        } else {
            System.out.println("Failed to create car. Status: " + response.statusCode());
        }
    }

    private static void handleAddFuel(String[] args) throws Exception {
        String carIdStr = getArgValue(args, "--carId");
        String litersStr = getArgValue(args, "--liters");
        String priceStr = getArgValue(args, "--price");
        String odometerStr = getArgValue(args, "--odometer");

        if (carIdStr == null || litersStr == null || priceStr == null || odometerStr == null) {
            System.out.println("Usage: add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
            return;
        }

        long carId = Long.parseLong(carIdStr);
        double liters = Double.parseDouble(litersStr);
        double price = Double.parseDouble(priceStr);
        int odometer = Integer.parseInt(odometerStr);

        Map<String, Object> fuelData = new HashMap<>();
        fuelData.put("liters", liters);
        fuelData.put("price", price);
        fuelData.put("odometer", odometer);

        String jsonBody = gson.toJson(fuelData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/cars/" + carId + "/fuel"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Fuel entry added successfully!");
        } else if (response.statusCode() == 404) {
            System.out.println("Car not found with ID: " + carId);
        } else {
            System.out.println("Failed to add fuel entry. Status: " + response.statusCode());
        }
    }

    private static void handleFuelStats(String[] args) throws Exception {
        String carIdStr = getArgValue(args, "--carId");

        if (carIdStr == null) {
            System.out.println("Usage: fuel-stats --carId <id>");
            return;
        }

        long carId = Long.parseLong(carIdStr);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/cars/" + carId + "/fuel/stats"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject stats = gson.fromJson(response.body(), JsonObject.class);
            double totalFuel = stats.get("totalFuel").getAsDouble();
            double totalCost = stats.get("totalCost").getAsDouble();
            double avgConsumption = stats.get("averageConsumption").getAsDouble();

            System.out.println();
            System.out.println("Fuel Statistics for Car #" + carId);
            System.out.println("-------------------------------");
            System.out.printf("Total fuel: %.1f L%n", totalFuel);
            System.out.printf("Total cost: %.2f%n", totalCost);
            System.out.printf("Average consumption: %.1f L/100km%n", avgConsumption);
            System.out.println();
        } else if (response.statusCode() == 404) {
            System.out.println("Car not found with ID: " + carId);
        } else {
            System.out.println("Failed to get stats. Status: " + response.statusCode());
        }
    }

    private static String getArgValue(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) {
                return args[i + 1];
            }
        }
        return null;
    }

    private static void printUsage() {
        System.out.println("Car Management CLI");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("  create-car --brand <brand> --model <model> --year <year>");
        System.out.println("  add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
        System.out.println("  fuel-stats --carId <id>");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  create-car --brand Toyota --model Corolla --year 2018");
        System.out.println("  add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000");
        System.out.println("  fuel-stats --carId 1");
    }
}