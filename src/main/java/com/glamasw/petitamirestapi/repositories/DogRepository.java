package com.glamasw.petitamirestapi.repositories;

import com.glamasw.petitamirestapi.entities.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends JpaRepository<Dog, Integer> {

}
