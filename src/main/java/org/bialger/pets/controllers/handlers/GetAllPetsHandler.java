package org.bialger.pets.controllers.handlers;

import org.bialger.pets.controllers.PetMappingHandler;
import org.bialger.pets.controllers.dto.GatewayRequest;
import org.bialger.pets.infrastructure.mappers.PetModelToDtoMapper;
import org.bialger.pets.models.PetModel;
import org.bialger.pets.services.contracts.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Handler for retrieving all pets
 * GET /api/pets
 */
@Component
public class GetAllPetsHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(GetAllPetsHandler.class);

    private final String method = "GET";
    private final String uriRegex = "^/$|^$"; // Empty path or /

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;

    @Autowired
    public GetAllPetsHandler(PetService petService, PetModelToDtoMapper petModelToDtoMapper) {
        this.petService = petService;
        this.petModelToDtoMapper = petModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        log.info("Processing request to get all pets");
        PageRequest pageRequest = extractPageRequest(request.getQueryParams());
        Page<PetModel> petModelPage = petService.getAllPets(pageRequest);
        return Optional.of(petModelPage.map(petModelToDtoMapper::petModelToPetDto));
    }

    private PageRequest extractPageRequest(Map<String, String[]> queryParams) {
        int page = 0;
        int size = 10;

        if (queryParams != null) {
            if (queryParams.containsKey("page")) {
                try {
                    page = Integer.parseInt(queryParams.get("page")[0]);
                } catch (Exception ignored) {}
            }
            if (queryParams.containsKey("size")) {
                try {
                    size = Integer.parseInt(queryParams.get("size")[0]);
                } catch (Exception ignored) {}
            }
        }

        return PageRequest.of(page, size);
    }
}
