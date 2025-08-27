package org.bialger.owners.controllers.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bialger.owners.controllers.OwnerMappingHandler;
import org.bialger.owners.controllers.dto.GatewayRequest;
import org.bialger.owners.controllers.dto.OwnerDto;
import org.bialger.owners.infrastructure.mappers.OwnerModelToDtoMapper;
import org.bialger.owners.models.OwnerModel;
import org.bialger.owners.services.contracts.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handler for creating a new owner
 * POST /api/owners
 */
@Component
public class CreateOwnerHandler implements OwnerMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateOwnerHandler.class);

    private final String method = "POST";
    private final String uriRegex = "^/$|^$"; // Empty path or /

    private final OwnerService ownerService;
    private final OwnerModelToDtoMapper ownerModelToDtoMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public CreateOwnerHandler(OwnerService ownerService,
                             OwnerModelToDtoMapper ownerModelToDtoMapper,
                             ObjectMapper objectMapper) {
        this.ownerService = ownerService;
        this.ownerModelToDtoMapper = ownerModelToDtoMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        log.info("Processing request to create a new owner");

        try {
            OwnerDto ownerDto = objectMapper.readValue(request.getBody(), OwnerDto.class);
            OwnerModel ownerModelToSave = ownerModelToDtoMapper.ownerDtoToOwnerModel(ownerDto);
            OwnerModel savedOwnerModel = ownerService.saveOwner(ownerModelToSave);

            return Optional.of(ownerModelToDtoMapper.ownerModelToOwnerDto(savedOwnerModel));
        } catch (Exception e) {
            log.error("Error creating owner: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating owner: " + e.getMessage(), e);
        }
    }
}
