package org.bialger.owners.controllers.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.bialger.owners.controllers.OwnerMappingHandler;
import org.bialger.owners.controllers.dto.GatewayRequest;
import org.bialger.owners.infrastructure.mappers.OwnerModelToDtoMapper;
import org.bialger.owners.models.OwnerModel;
import org.bialger.owners.services.contracts.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handler for retrieving owner by ID
 * GET /api/owners/{id}
 */
@Component
public class GetOwnerByIdHandler implements OwnerMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(GetOwnerByIdHandler.class);

    private final String method = "GET";
    private final String uriRegex = "^/\\d+$"; // /123

    private final OwnerService ownerService;
    private final OwnerModelToDtoMapper ownerModelToDtoMapper;

    @Autowired
    public GetOwnerByIdHandler(OwnerService ownerService, OwnerModelToDtoMapper ownerModelToDtoMapper) {
        this.ownerService = ownerService;
        this.ownerModelToDtoMapper = ownerModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        Long id = Long.parseLong(path.substring(1));

        log.info("Processing request to get owner by ID: {}", id);
        OwnerModel ownerModel = ownerService.getOwnerById(id)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + id));

        return Optional.of(ownerModelToDtoMapper.ownerModelToOwnerDto(ownerModel));
    }
}
