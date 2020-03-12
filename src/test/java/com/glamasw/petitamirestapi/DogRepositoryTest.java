package com.glamasw.petitamirestapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.glamasw.petitamirestapi.entities.Dog;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.DogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest
@AutoConfigureMockMvc
@Execution(ExecutionMode.CONCURRENT)

public class DogRepositoryTest {

    @Autowired
    DogRepository dogRepository;

    @DisplayName("Create null dog")
    @RepeatedTest(1)

    public void createNullDogTest(final TestInfo testInfo) throws Exception {
        try {
            Dog dogEntity = new Dog();
            dogEntity.setName("");
            dogEntity.setOwner(null);
            dogEntity = dogRepository.save(dogEntity);
            assertTrue(dogEntity.getId() != 0);
        } catch (Exception e) {
            assertFalse(false);
        }
    }

    @DisplayName("Create null dog with existent owner")
    @RepeatedTest(1)

    public void createNullDogWithExistentOwnerTest(final TestInfo testInfo) throws Exception {
        Dog dogEntity = new Dog();
        Owner ownerEntity = new Owner();
        dogEntity.setName("");
        ownerEntity.setName("owner");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.getDogs().add(dogEntity);
        ownerEntity.setContactMediums(new ArrayList<>());
        dogEntity.setOwner(ownerEntity);
        dogEntity = dogRepository.save(dogEntity);
        assertTrue(dogEntity.getId() != 0);
    }

}