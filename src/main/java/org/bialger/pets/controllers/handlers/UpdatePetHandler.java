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
 * Handler for updating a pet
 * PUT /api/pets/{id}
 */
@Component
public class UpdatePetHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdatePetHandler.class);

    private final String method = "PUT";
    private final String uriRegex = "^/\\d+$"; // /123

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public UpdatePetHandler(PetService petService,
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

        String path = request.getRequestPath();
        Long id = Long.parseLong(path.substring(1));

        log.info("Processing request to update pet with ID: {}", id);

        try {
            PetDto petDto = objectMapper.readValue(request.getBody(), PetDto.class);
            petDto.setId(id); // Устанавливаем ID из пути

            PetModel petModelToUpdate = petModelToDtoMapper.petDtoToPetModel(petDto);
            PetModel updatedPetModel = petService.savePet(petModelToUpdate);

            return Optional.of(petModelToDtoMapper.petModelToPetDto(updatedPetModel));
        } catch (Exception e) {
            log.error("Error updating pet: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating pet: " + e.getMessage(), e);
        }
    }
}
