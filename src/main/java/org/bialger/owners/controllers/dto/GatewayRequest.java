package org.bialger.owners.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRequest {
    private String httpMethod;
    private String requestPath;
    private Map<String, String[]> queryParams;
    private String body;
    private String replyTo;
    private String correlationId;
}

