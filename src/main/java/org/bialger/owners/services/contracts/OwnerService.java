package org.bialger.owners.services.contracts;

import org.bialger.owners.models.OwnerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OwnerService {
    Optional<OwnerModel> getOwnerById(Long id);

    Page<OwnerModel> getAllOwners(Pageable pageable);

    OwnerModel saveOwner(OwnerModel ownerModel);

    void deleteOwner(Long id);

    Page<OwnerModel> findOwnersByNameContaining(String nameFragment, Pageable pageable);
}
