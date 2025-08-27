package org.bialger.owners.services.application;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bialger.owners.entities.Owner;
import org.bialger.owners.infrastructure.mappers.OwnerEntityToModelMapper;
import org.bialger.owners.models.OwnerModel;
import org.bialger.owners.repositories.OwnerRepository;
import org.bialger.owners.services.contracts.OwnerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerEntityToModelMapper ownerMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<OwnerModel> getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .map(ownerMapper::ownerEntityToOwnerModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OwnerModel> getAllOwners(Pageable pageable) {
        return ownerRepository.findAll(pageable)
                .map(ownerMapper::ownerEntityToOwnerModel);
    }

    @Override
    public OwnerModel saveOwner(OwnerModel ownerModel) {
        Owner owner = ownerMapper.ownerModelToOwnerEntity(ownerModel);
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.ownerEntityToOwnerModel(savedOwner);
    }

    @Override
    public void deleteOwner(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new EntityNotFoundException("Owner with id " + id + " not found, cannot delete.");
        }
        ownerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OwnerModel> findOwnersByNameContaining(String nameFragment, Pageable pageable) {
        return ownerRepository.findByNameContainingIgnoreCase(nameFragment, pageable)
                .map(ownerMapper::ownerEntityToOwnerModel);
    }
}
