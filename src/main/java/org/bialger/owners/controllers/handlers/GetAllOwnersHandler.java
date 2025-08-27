package org.bialger.owners.controllers.handlers;

import org.bialger.owners.controllers.OwnerMappingHandler;
import org.bialger.owners.controllers.dto.GatewayRequest;
import org.bialger.owners.infrastructure.mappers.OwnerModelToDtoMapper;
import org.bialger.owners.models.OwnerModel;
import org.bialger.owners.services.contracts.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Handler for retrieving all owners
 * GET /api/owners
 */
@Component
public class GetAllOwnersHandler implements OwnerMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(GetAllOwnersHandler.class);

    private final String method = "GET";
    private final String uriRegex = "^/$|^$"; // Empty path or /

    private final OwnerService ownerService;
    private final OwnerModelToDtoMapper ownerModelToDtoMapper;

    @Autowired
    public GetAllOwnersHandler(OwnerService ownerService, OwnerModelToDtoMapper ownerModelToDtoMapper) {
        this.ownerService = ownerService;
        this.ownerModelToDtoMapper = ownerModelToDtoMapper;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        log.info("Processing request to get all owners");
        PageRequest pageRequest = extractPageRequest(request.getQueryParams());
        Page<OwnerModel> ownerModelPage = ownerService.getAllOwners(pageRequest);
        return Optional.of(ownerModelPage.map(ownerModelToDtoMapper::ownerModelToOwnerDto));
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
