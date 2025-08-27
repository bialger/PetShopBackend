package org.bialger.pets.controllers.handlers;

import org.bialger.pets.controllers.PetMappingHandler;
import org.bialger.pets.controllers.dto.GatewayRequest;
import org.bialger.pets.entities.Color;
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
 * Handler for retrieving pets by color
 * GET /api/pets/color/{color}
 */
@Component
public class GetPetsByColorHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(GetPetsByColorHandler.class);

    private final String method = "GET";
    private final String uriRegex = "^/color/[A-Za-z]+$"; // /color/RED

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;

    @Autowired
    public GetPetsByColorHandler(PetService petService, PetModelToDtoMapper petModelToDtoMapper) {
        this.petService = petService;
        this.petModelToDtoMapper = petModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        String colorStr = path.substring("/color/".length());

        try {
            Color color = Color.valueOf(colorStr.toUpperCase());
            log.info("Processing request to get pets by color: {}", color);

            PageRequest pageRequest = extractPageRequest(request.getQueryParams());
            Page<PetModel> petModelPage = petService.findPetsByColor(color, pageRequest);

            return Optional.of(petModelPage.map(petModelToDtoMapper::petModelToPetDto));
        } catch (IllegalArgumentException e) {
            log.error("Invalid color: {}", colorStr);
            throw new IllegalArgumentException("Invalid color: " + colorStr + ". Available colors: " + Color.getAllColorNames());
        }
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
