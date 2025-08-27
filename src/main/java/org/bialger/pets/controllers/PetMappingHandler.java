package org.bialger.pets.controllers;

import org.bialger.pets.controllers.dto.GatewayRequest;

import java.util.Optional;

public interface PetMappingHandler {

    Optional<Object> handleRequest(GatewayRequest request);
}
