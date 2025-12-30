package com.example.carmanagement;

import com.example.carmanagement.service.CarService;
import com.example.carmanagement.servlet.FuelStatsServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CarManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarManagementApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServlet(CarService carService) {
        FuelStatsServlet servlet = new FuelStatsServlet();
        servlet.setCarService(carService);

        ServletRegistrationBean<FuelStatsServlet> registration =
                new ServletRegistrationBean<>(servlet, "/servlet/fuel-stats");
        registration.setLoadOnStartup(1);
        return registration;
    }
}