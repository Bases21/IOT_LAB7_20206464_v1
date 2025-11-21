package com.example.lab720206464;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Lab720206464Application {

    public static void main(String[] args) {
        System.setProperty("eureka.instance.hostname", "localhost");
        SpringApplication.run(Lab720206464Application.class, args);
    }

}
