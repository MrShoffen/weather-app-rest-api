package org.mrshoffen.weather;

import org.mrshoffen.weather.service.OpenWeatherApiService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WeatherAppRestApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WeatherAppRestApiApplication.class, args);


        OpenWeatherApiService bean = context.getBean(OpenWeatherApiService.class);



        System.out.println();
    }

}
