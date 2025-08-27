package org.bialger.owners.controllers;

import org.bialger.owners.controllers.dto.GatewayRequest;

import java.util.Optional;

public interface OwnerMappingHandler {

    Optional<Object> handleRequest(GatewayRequest request);
}
