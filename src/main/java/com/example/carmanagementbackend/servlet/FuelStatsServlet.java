package com.example.carmanagementbackend.servlet;

import com.example.carmanagementbackend.model.FuelStats;
import com.example.carmanagementbackend.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class FuelStatsServlet extends HttpServlet {

    private CarService carService;
    private ObjectMapper objectMapper;

    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Manually parse carId from query parameters
        String carIdParam = request.getParameter("carId");

        // Set Content-Type explicitly
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Validate carId parameter
        if (carIdParam == null || carIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"carId parameter is required\"}");
            return;
        }

        try {
            Long carId = Long.parseLong(carIdParam);

            // Use the same Service layer as REST API
            Optional<FuelStats> stats = carService.getFuelStats(carId);

            if (stats.isPresent()) {
                // Set status code explicitly
                response.setStatus(HttpServletResponse.SC_OK);
                // Convert stats to JSON and write response
                String jsonResponse = objectMapper.writeValueAsString(stats.get());
                response.getWriter().write(jsonResponse);
            } else {
                // Set 404 status explicitly
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Car not found with id: " + carId + "\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid carId format\"}");
        }
    }
}