package org.bialger.gateway.services.contracts;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Interface for a service responsible for processing and forwarding requests from Gateway to microservices
 */
public interface GatewayService {

    /**
     * Forwards an HTTP request to the appropriate microservice through the messaging system
     *
     * @param httpRequest HTTP request
     * @param body        request body
     * @param routingKey  routing key for messages
     * @param serviceName microservice name
     * @return response from the microservice as ResponseEntity
     * @throws JsonProcessingException if there are issues with JSON processing
     */
    ResponseEntity<?> forwardRequest(HttpServletRequest httpRequest, String body, String routingKey, String serviceName) throws JsonProcessingException;
}
