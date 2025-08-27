package org.bialger.owners.controllers.handlers;

import org.bialger.owners.controllers.OwnerMappingHandler;
import org.bialger.owners.controllers.dto.GatewayRequest;
import org.bialger.owners.services.contracts.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handler for deleting an owner
 * DELETE /api/owners/{id}
 */
@Component
public class DeleteOwnerHandler implements OwnerMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(DeleteOwnerHandler.class);

    private final String method = "DELETE";
    private final String uriRegex = "^/\\d+$"; // /123

    private final OwnerService ownerService;

    @Autowired
    public DeleteOwnerHandler(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        Long id = Long.parseLong(path.substring(1));

        log.info("Processing request to delete owner with ID: {}", id);

        try {
            ownerService.deleteOwner(id);
            return Optional.of("Owner with id " + id + " deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting owner: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting owner: " + e.getMessage(), e);
        }
    }
}
