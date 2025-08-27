package org.bialger.gateway.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.bialger.gateway.config.RabbitMqConfig;
import org.bialger.gateway.services.contracts.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetGatewayController {

    private static final String SERVICE_NAME = "pets";

    private final GatewayService gatewayService;
    private final RabbitMqConfig rabbitMqConfig;

    @Autowired
    public PetGatewayController(GatewayService gatewayService, RabbitMqConfig rabbitMqConfig) {
        this.gatewayService = gatewayService;
        this.rabbitMqConfig = rabbitMqConfig;
    }

    @RequestMapping("/**")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> handlePetRequests(HttpServletRequest httpRequest, @RequestBody(required = false) String body) throws JsonProcessingException {
        return gatewayService.forwardRequest(httpRequest, body, rabbitMqConfig.getPetsRequestsRoutingKey(), SERVICE_NAME);
    }
}
