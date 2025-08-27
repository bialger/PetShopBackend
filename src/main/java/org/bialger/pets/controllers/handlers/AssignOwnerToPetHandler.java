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
 * Handler for assigning an owner to a pet
 * PUT /api/pets/{id}/owner/{ownerId}
 */
@Component
public class AssignOwnerToPetHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(AssignOwnerToPetHandler.class);

    private final String method = "PUT";
    private final String uriRegex = "^/\\d+/owner/\\d+$"; // /123/owner/456

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;

    @Autowired
    public AssignOwnerToPetHandler(PetService petService, PetModelToDtoMapper petModelToDtoMapper) {
        this.petService = petService;
        this.petModelToDtoMapper = petModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        String[] parts = path.substring(1).split("/owner/");
        Long petId = Long.parseLong(parts[0]);
        Long ownerId = Long.parseLong(parts[1]);

        log.info("Processing request to assign owner ID: {} to pet ID: {}", ownerId, petId);

        try {
            PetModel updatedPetModel = petService.assignOwnerToPet(petId, ownerId);
            return Optional.of(petModelToDtoMapper.petModelToPetDto(updatedPetModel));
        } catch (Exception e) {
            log.error("Error assigning owner to pet: {}", e.getMessage(), e);
            throw new RuntimeException("Error assigning owner to pet: " + e.getMessage(), e);
        }
    }
}
