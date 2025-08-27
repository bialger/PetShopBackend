package org.bialger.owners;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class OwnerMicroservice {
    public static void main(String[] args) {
        SpringApplication.run(OwnerMicroservice.class, args);
    }
}
