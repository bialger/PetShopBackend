package org.bialger.pets.controllers.handlers;

import jakarta.persistence.EntityNotFoundException;
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
 * Handler for retrieving pet by ID
 * GET /api/pets/{id}
 */
@Component
public class GetPetByIdHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(GetPetByIdHandler.class);

    private final String method = "GET";
    private final String uriRegex = "^/\\d+$"; // /123

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;

    @Autowired
    public GetPetByIdHandler(PetService petService, PetModelToDtoMapper petModelToDtoMapper) {
        this.petService = petService;
        this.petModelToDtoMapper = petModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        Long id = Long.parseLong(path.substring(1));

        log.info("Processing request to get pet by ID: {}", id);

        PetModel petModel = petService.getPetById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + id));

        return Optional.of(petModelToDtoMapper.petModelToPetDto(petModel));
    }
}
