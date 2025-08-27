package org.bialger.pets.controllers.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bialger.pets.controllers.PetMappingHandler;
import org.bialger.pets.controllers.dto.GatewayRequest;
import org.bialger.pets.controllers.dto.PetDto;
import org.bialger.pets.infrastructure.mappers.PetModelToDtoMapper;
import org.bialger.pets.models.PetModel;
import org.bialger.pets.services.contracts.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handler for creating a new pet
 * POST /api/pets
 */
@Component
public class CreatePetHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(CreatePetHandler.class);

    private final String method = "POST";
    private final String uriRegex = "^/$|^$"; // Empty path or /

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public CreatePetHandler(PetService petService,
                           PetModelToDtoMapper petModelToDtoMapper,
                           ObjectMapper objectMapper) {
        this.petService = petService;
        this.petModelToDtoMapper = petModelToDtoMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        log.info("Processing request to create a new pet");

        try {
            PetDto petDto = objectMapper.readValue(request.getBody(), PetDto.class);
            PetModel petModelToSave = petModelToDtoMapper.petDtoToPetModel(petDto);
            PetModel savedPetModel = petService.savePet(petModelToSave);

            return Optional.of(petModelToDtoMapper.petModelToPetDto(savedPetModel));
        } catch (Exception e) {
            log.error("Error creating pet: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating pet: " + e.getMessage(), e);
        }
    }
}
