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
@RequestMapping("/api/owners")
public class OwnerGatewayController {

    private static final String SERVICE_NAME = "owners";

    private final GatewayService gatewayService;
    private final RabbitMqConfig rabbitMqConfig;

    @Autowired
    public OwnerGatewayController(GatewayService gatewayService, RabbitMqConfig rabbitMqConfig) {
        this.gatewayService = gatewayService;
        this.rabbitMqConfig = rabbitMqConfig;
    }

    @RequestMapping("/**")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> handleOwnerRequests(HttpServletRequest httpRequest, @RequestBody(required = false) String body) throws JsonProcessingException {
        return gatewayService.forwardRequest(httpRequest, body, rabbitMqConfig.getOwnersRequestsRoutingKey(), SERVICE_NAME);
    }
}
