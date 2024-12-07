package org.mrshoffen.weather;

import jakarta.persistence.EntityManager;
import org.mrshoffen.weather.dto.UserRegistrationDto;
import org.mrshoffen.weather.entity.User;
import org.mrshoffen.weather.repository.UserRepository;
import org.mrshoffen.weather.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WeatherAppRestApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext config = SpringApplication.run(WeatherAppRestApiApplication.class, args);
        var bean = config.getBean(UserService.class);

        UserRegistrationDto dto = new UserRegistrationDto("mrshoffen3", "dfjsldfjsdl");

        bean.register(dto);

        System.out.println();

    }

}
