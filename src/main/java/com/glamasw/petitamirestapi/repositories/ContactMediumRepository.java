package com.glamasw.petitamirestapi.repositories;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMediumRepository extends JpaRepository<ContactMedium, Integer> {
}
