package org.mrshoffen.weather;

import org.mrshoffen.weather.dto.UserLoginDto;
import org.mrshoffen.weather.dto.UserRegistrationDto;
import org.mrshoffen.weather.service.AuthenticationService;
import org.mrshoffen.weather.service.SessionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WeatherAppRestApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext config = SpringApplication.run(WeatherAppRestApiApplication.class, args);
        var auth = config.getBean(AuthenticationService.class);

        UserRegistrationDto dto = new UserRegistrationDto("zhuk", "1337");

//        auth.register(dto);

        UserLoginDto zhuk = new UserLoginDto("zhuk", "1337");

        auth.login(zhuk);


        System.out.println();


    }

}
