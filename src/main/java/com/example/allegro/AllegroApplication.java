package com.example.allegro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AllegroApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllegroApplication.class, args);
    }

}
