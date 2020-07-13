package com.glamasw.petitamirestapi.repositories;

import java.util.List;
import java.util.Optional;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMediumRepository extends JpaRepository<ContactMedium, Integer> {
    List<ContactMedium> findByOwnerId(int id);
}
