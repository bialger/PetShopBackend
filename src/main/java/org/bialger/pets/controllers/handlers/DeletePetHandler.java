package org.bialger.pets.controllers.handlers;

import org.bialger.pets.controllers.PetMappingHandler;
import org.bialger.pets.controllers.dto.GatewayRequest;
import org.bialger.pets.services.contracts.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handler for deleting a pet
 * DELETE /api/pets/{id}
 */
@Component
public class DeletePetHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(DeletePetHandler.class);

    private final String method = "DELETE";
    private final String uriRegex = "^/\\d+$"; // /123

    private final PetService petService;

    @Autowired
    public DeletePetHandler(PetService petService) {
        this.petService = petService;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        Long id = Long.parseLong(path.substring(1));

        log.info("Processing request to delete pet with ID: {}", id);

        try {
            petService.deletePet(id);
            return Optional.of("Pet with id " + id + " deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting pet: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting pet: " + e.getMessage(), e);
        }
    }
}

