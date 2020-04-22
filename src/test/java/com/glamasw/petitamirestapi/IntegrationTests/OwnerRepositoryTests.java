package com.glamasw.petitamirestapi.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Dog;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import com.glamasw.petitamirestapi.services.DogService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Optional;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest
@AutoConfigureMockMvc
@Execution(ExecutionMode.CONCURRENT)

public class OwnerRepositoryTests {

    @Autowired
    OwnerRepository ownerRepository;

    @Test
    @DisplayName("Find Owner by DNI")
    void findByDni() throws Exception {

        try {

            for (int i = 1; i <= 5; i++) {
                Owner owner = new Owner();
                owner.setName("Chocoperro Owner "+ i);
                owner.setDni(49578150+i);
                owner.setDogs(new ArrayList<>());
                owner.setContactMediums(new ArrayList<>());
                ownerRepository.save(owner);
                System.out.println(i);
            }
            Optional<Owner> optionalOwner = ownerRepository.findByDni(49578151);
            Owner resultOwner = optionalOwner.get();
            assertTrue(resultOwner.getName().contentEquals("Chocoperro Owner 1"));
        } catch (Exception e) {
            throw new Exception();
        }
    }
}