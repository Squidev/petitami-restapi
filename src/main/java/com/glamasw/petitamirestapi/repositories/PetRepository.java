package com.glamasw.petitamirestapi.repositories;

import com.glamasw.petitamirestapi.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    Optional<Pet> findByUuid(String uuid);

    List<Pet> findByOwnerId(int id);
}
