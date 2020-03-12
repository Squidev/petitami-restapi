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
public class JPAIntegrityTest {

    @Autowired
    DogRepository dogRepository;

    @DisplayName("Dog-Owner bidireccional relationship")
    @RepeatedTest(1)
    public void dogOwnerBidireccionalTest(final TestInfo testInfo) throws Exception {
        //Initialización de entidades
        Dog dogEntity = new Dog();
        Owner ownerEntity = new Owner();
        //Seteo de atributos de entidades
        dogEntity.setName("Chocoperro");
        ownerEntity.setName("Chocoperro Owner");
        ownerEntity.setDni(47184516);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.getDogs().add(dogEntity);
        ownerEntity.setContactMediums(new ArrayList<>());
        //Asignación de owner al choco
        dogEntity.setOwner(ownerEntity);
        //Persistencia del choco
        dogEntity = dogRepository.save(dogEntity);
        //Test de bidireccionalidad
        assertFalse(ownerEntity.getDogs().isEmpty());
    }

    //Test de Owner sin ContactMediums
}