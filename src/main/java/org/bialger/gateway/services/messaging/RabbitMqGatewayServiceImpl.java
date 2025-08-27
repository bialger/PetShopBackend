package org.bialger.gateway.services.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.bialger.gateway.config.RabbitMqConfig;
import org.bialger.gateway.controllers.dto.GatewayRequest;
import org.bialger.gateway.services.contracts.GatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
public class RabbitMqGatewayServiceImpl implements GatewayService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqGatewayServiceImpl.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final RabbitMqConfig rabbitMqConfig;

    @Autowired
    public RabbitMqGatewayServiceImpl(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, RabbitMqConfig rabbitMqConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.rabbitMqConfig = rabbitMqConfig;
    }

    public ResponseEntity<?> forwardRequest(HttpServletRequest httpRequest, String body, String routingKey, String serviceName) throws JsonProcessingException {
        String fullPath = httpRequest.getRequestURI();
        String servicePath = "/api/" + serviceName;
        String subPath = "/";
        if (fullPath.length() > servicePath.length()) {
            subPath = fullPath.substring(servicePath.length());
        }

        GatewayRequest gatewayRequest = new GatewayRequest();
        gatewayRequest.setHttpMethod(httpRequest.getMethod());
        gatewayRequest.setRequestPath(subPath);
        gatewayRequest.setQueryParams(convertQueryMap(httpRequest.getParameterMap()));
        gatewayRequest.setBody(body);
        gatewayRequest.setReplyTo(rabbitMqConfig.getGatewayRepliesRoutingKey());
        gatewayRequest.setCorrelationId(UUID.randomUUID().toString());

        log.info("Gateway: Forwarding {} request for path '{}' (subpath '{}') to {} with correlationId: {}",
                gatewayRequest.getHttpMethod(), fullPath, subPath, serviceName, gatewayRequest.getCorrelationId());
        if (body != null && !body.isEmpty()) {
            log.debug("Gateway: Request body: {}", body);
        }

        String jsonResponse = (String) rabbitTemplate.convertSendAndReceive(
                rabbitMqConfig.getExchangeName(),
                routingKey,
                gatewayRequest
        );

        if (jsonResponse == null) {
            log.warn("Gateway: Received null response from {} service for correlationId: {}. Request path: {}",
                    serviceName, gatewayRequest.getCorrelationId(), gatewayRequest.getRequestPath());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No response from " + serviceName + " service.");
        }

        log.info("Gateway: Received response from {} service for correlationId: {}. Length: {}",
                serviceName, gatewayRequest.getCorrelationId(), jsonResponse.length());
        log.debug("Gateway: Response body: {}", jsonResponse);

        if (HttpMethod.DELETE.name().equalsIgnoreCase(gatewayRequest.getHttpMethod())) {
            if (jsonResponse.isEmpty() || jsonResponse.toLowerCase().contains("successfully")) {
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Gateway: DELETE for {} returned a non-standard success-like response: {}", gatewayRequest.getRequestPath(), jsonResponse);
                return ResponseEntity.ok(jsonResponse);
            }
        }

        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        if (jsonNode.has("error") && jsonNode.has("status")) {
            int status = jsonNode.get("status").asInt();
            String error = jsonNode.get("error").asText();
            throw new ResponseStatusException(HttpStatus.valueOf(status), error);
        }

        JsonNode responseObject = objectMapper.readTree(jsonResponse);
        return ResponseEntity.ok(responseObject);
    }

    private Map<String, String[]> convertQueryMap(Map<String, String[]> springQueryMap) {
        return springQueryMap;
    }
}
