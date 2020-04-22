package com.glamasw.petitamirestapi.UnitTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.glamasw.petitamirestapi.dtos.ContactMediumDTO;
import com.glamasw.petitamirestapi.dtos.DogDTO;
import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Dog;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.services.DogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
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
public class DogServiceTests {

    @Autowired
    DogService dogService;

    @Test
    @DisplayName("DogService save() method test: No contact mediums provided")
    public void saveNoContactMediums() throws Exception {
        //Entities init
        DogDTO dogDTO = new DogDTO();
        //Atributes setting
        dogDTO.setDogName("Chocoperro");
        dogDTO.setOwnerName("Cochoperro Owner");
        dogDTO.setOwnerDNI(49784584);
        dogDTO.setContactMediumDTOS(new ArrayList<>());
        //Persist dogDTO
        try {
            dogDTO = dogService.save(dogDTO);
        } catch (Exception e) {
            throw new Exception();
        }
        assertTrue(dogDTO.getContactMediumDTOS().isEmpty());
    }


}
