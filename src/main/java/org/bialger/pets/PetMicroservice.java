package org.bialger.pets;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class PetMicroservice {
    public static void main(String[] args) {
        SpringApplication.run(PetMicroservice.class, args);
    }
}
