package org.bialger.pets.controllers.handlers;

import org.bialger.pets.controllers.PetMappingHandler;
import org.bialger.pets.controllers.dto.GatewayRequest;
import org.bialger.pets.infrastructure.mappers.PetModelToDtoMapper;
import org.bialger.pets.models.PetModel;
import org.bialger.pets.services.contracts.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handler for transferring a pet to a new owner
 * PUT /api/pets/{id}/transfer/{newOwnerId}
 */
@Component
public class TransferPetToNewOwnerHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(TransferPetToNewOwnerHandler.class);

    private final String method = "PUT";
    private final String uriRegex = "^/\\d+/transfer/\\d+$"; // /123/transfer/456

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;

    @Autowired
    public TransferPetToNewOwnerHandler(PetService petService, PetModelToDtoMapper petModelToDtoMapper) {
        this.petService = petService;
        this.petModelToDtoMapper = petModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        String[] parts = path.substring(1).split("/transfer/");
        Long petId = Long.parseLong(parts[0]);
        Long newOwnerId = Long.parseLong(parts[1]);

        log.info("Processing request to transfer pet ID: {} to new owner ID: {}", petId, newOwnerId);

        try {
            PetModel updatedPetModel = petService.transferPetToNewOwner(petId, newOwnerId);
            return Optional.of(petModelToDtoMapper.petModelToPetDto(updatedPetModel));
        } catch (Exception e) {
            log.error("Error transferring pet to new owner: {}", e.getMessage(), e);
            throw new RuntimeException("Error transferring pet to new owner: " + e.getMessage(), e);
        }
    }
}
