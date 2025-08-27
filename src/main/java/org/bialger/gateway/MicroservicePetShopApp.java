package org.bialger.gateway;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class MicroservicePetShopApp {
    public static void main(String[] args) {
        SpringApplication.run(MicroservicePetShopApp.class, args);
    }
}
