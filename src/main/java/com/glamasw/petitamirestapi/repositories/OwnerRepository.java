package com.glamasw.petitamirestapi.repositories;

import com.glamasw.petitamirestapi.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    @Query("from Owner o where o.dni=:dni")
    Optional<Owner> findByDni(@Param("dni") int dni);
}
