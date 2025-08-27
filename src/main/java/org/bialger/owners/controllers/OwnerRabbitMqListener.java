package org.bialger.owners.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bialger.owners.controllers.dto.GatewayRequest;
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
public class OwnerRabbitMqListener {

    private static final Logger log = LoggerFactory.getLogger(OwnerRabbitMqListener.class);

    private final ObjectMapper objectMapper;
    private final List<OwnerMappingHandler> handlers;

    @Autowired
    public OwnerRabbitMqListener(ObjectMapper objectMapper,
                                 List<OwnerMappingHandler> handlers) {
        this.objectMapper = objectMapper;
        this.handlers = handlers;
        log.info("Owner Service: initialized with {} request handlers", handlers.size());
    }

    @RabbitListener(queues = "${app.messaging.request-queue-name}")
    public String handleOwnerRequest(@Payload GatewayRequest request) {
        String httpMethod = request.getHttpMethod();
        String path = request.getRequestPath();

        log.info("Owner Service: Received request with correlationId: {}, method: {}, path: {}",
                request.getCorrelationId(), httpMethod, path);

        try {
            for (OwnerMappingHandler handler : handlers) {
                Optional<Object> result = handler.handleRequest(request);
                if (result.isPresent()) {
                    log.info("Owner Service: Successfully processed request for correlationId: {}, method: {}, path: {}",
                            request.getCorrelationId(), httpMethod, path);
                    return objectMapper.writeValueAsString(result.get());
                }
            }

            log.warn("Owner Service: No handler found for request path: {} and method: {}", path, httpMethod);
            return createErrorJson("No handler for request: " + path + " [" + httpMethod + "]", 404, request.getRequestPath());

        } catch (Exception e) {
            log.error("Owner Service: Error processing request for correlationId: {}: {}",
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
            log.error("Owner Service: Failed to serialize error message", jpe);
            return "{\"error\":\"Internal server error\",\"status\":500}";
        }
    }
}

