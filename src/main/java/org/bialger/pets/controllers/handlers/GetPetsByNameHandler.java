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
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Handler for retrieving pets by name
 * GET /api/pets/name/{name}
 */
@Component
public class GetPetsByNameHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(GetPetsByNameHandler.class);

    private final String method = "GET";
    private final String uriRegex = "^/name/.+$"; // /name/Rex

    private final PetService petService;
    private final PetModelToDtoMapper petModelToDtoMapper;

    @Autowired
    public GetPetsByNameHandler(PetService petService, PetModelToDtoMapper petModelToDtoMapper) {
        this.petService = petService;
        this.petModelToDtoMapper = petModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        String name = path.substring("/name/".length());

        if (!StringUtils.hasText(name)) {
            log.error("Name parameter is empty");
            throw new IllegalArgumentException("Name parameter is required");
        }

        log.info("Processing request to get pets by name: {}", name);

        PageRequest pageRequest = extractPageRequest(request.getQueryParams());
        Page<PetModel> petModelPage = petService.findPetsByNameContaining(name, pageRequest);

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
