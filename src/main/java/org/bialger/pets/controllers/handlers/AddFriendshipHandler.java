package org.bialger.pets.controllers.handlers;

import org.bialger.pets.controllers.PetMappingHandler;
import org.bialger.pets.controllers.dto.GatewayRequest;
import org.bialger.pets.services.contracts.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler for adding friendship between pets
 * PUT /api/pets/{id}/friend/{friendId}
 */
@Component
public class AddFriendshipHandler implements PetMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(AddFriendshipHandler.class);

    private final String method = "PUT";
    private final String uriRegex = "^/\\d+/friend/\\d+$"; // /123/friend/456
    private final Pattern pattern = Pattern.compile("^/(\\d+)/friend/(\\d+)$");

    private final PetService petService;

    @Autowired
    public AddFriendshipHandler(PetService petService) {
        this.petService = petService;
    }

    @Override
    public Optional<Object> handleRequest(GatewayRequest request) {
        if (!method.equalsIgnoreCase(request.getHttpMethod()) || !request.getRequestPath().matches(uriRegex)) {
            return Optional.empty();
        }

        String path = request.getRequestPath();
        Matcher matcher = pattern.matcher(path);

        if (matcher.find()) {
            Long petId = Long.parseLong(matcher.group(1));
            Long friendId = Long.parseLong(matcher.group(2));

            log.info("Processing request to add friendship between pets {} and {}", petId, friendId);

            try {
                petService.addFriendship(petId, friendId);
                return Optional.of("Friendship added between pet " + petId + " and " + friendId);
            } catch (Exception e) {
                log.error("Error adding friendship between pets: {}", e.getMessage(), e);
                throw new RuntimeException("Error adding friendship: " + e.getMessage(), e);
            }
        }

        return Optional.empty();
    }
}

