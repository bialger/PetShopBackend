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
 * Handler for updating an owner
 * PUT /api/owners/{id}
 */
@Component
public class UpdateOwnerHandler implements OwnerMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateOwnerHandler.class);

    private final String method = "PUT";
    private final String uriRegex = "^/\\d+$"; // /123

    private final OwnerService ownerService;
    private final OwnerModelToDtoMapper ownerModelToDtoMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public UpdateOwnerHandler(OwnerService ownerService,
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

        String path = request.getRequestPath();
        Long id = Long.parseLong(path.substring(1));

        log.info("Processing request to update owner with ID: {}", id);

        try {
            OwnerDto ownerDto = objectMapper.readValue(request.getBody(), OwnerDto.class);
            ownerDto.setId(id);

            OwnerModel ownerModelToUpdate = ownerModelToDtoMapper.ownerDtoToOwnerModel(ownerDto);
            OwnerModel updatedOwnerModel = ownerService.saveOwner(ownerModelToUpdate);

            return Optional.of(ownerModelToDtoMapper.ownerModelToOwnerDto(updatedOwnerModel));
        } catch (Exception e) {
            log.error("Error updating owner: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating owner: " + e.getMessage(), e);
        }
    }
}
