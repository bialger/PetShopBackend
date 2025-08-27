package org.bialger.pets.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bialger.pets.controllers.dto.GatewayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PetRabbitMqListener {

    private static final Logger log = LoggerFactory.getLogger(PetRabbitMqListener.class);

    private final ObjectMapper objectMapper;
    private final List<PetMappingHandler> handlers;

    @Autowired
    public PetRabbitMqListener(ObjectMapper objectMapper,
                               List<PetMappingHandler> handlers) {
        this.objectMapper = objectMapper;
        this.handlers = handlers;
        log.info("Pet Service: initialized with {} request handlers", handlers.size());
    }

    // Используем SpEL для получения значения константы из конфигурации
    @RabbitListener(queues = "${app.messaging.request-queue-name}")
    public String handlePetRequest(@Payload GatewayRequest request) {
        String httpMethod = request.getHttpMethod();
        String path = request.getRequestPath();

        log.info("Pet Service: Received request with correlationId: {}, method: {}, path: {}",
                request.getCorrelationId(), httpMethod, path);

        try {
            for (PetMappingHandler handler : handlers) {
                Optional<Object> result = handler.handleRequest(request);
                if (result.isPresent()) {
                    log.info("Pet Service: Successfully processed request for correlationId: {}, method: {}, path: {}",
                            request.getCorrelationId(), httpMethod, path);
                    return objectMapper.writeValueAsString(result.get());
                }
            }

            log.warn("Pet Service: No handler found for request path: {} and method: {}", path, httpMethod);
            return createErrorJson("No handler for request: " + path + " [" + httpMethod + "]", 404, request.getRequestPath());

        } catch (Exception e) {
            log.error("Pet Service: Error processing request for correlationId: {}: {}",
                    request.getCorrelationId(), e.getMessage(), e);
            return createErrorJson("Error processing request: " + e.getMessage(), 500, request.getRequestPath());
        }
    }

    private String createErrorJson(String errorMessage, int statusCode, String path) {
        try {
            Map<String, Object> errorDetails = Map.of(
                    "error", errorMessage,
                    "status", statusCode,
                    "path", path
            );
            return objectMapper.writeValueAsString(errorDetails);
        } catch (JsonProcessingException jpe) {
            log.error("Pet Service: Failed to serialize error message", jpe);
            return "{\"error\":\"Internal server error\",\"status\":500}";
        }
    }
}
