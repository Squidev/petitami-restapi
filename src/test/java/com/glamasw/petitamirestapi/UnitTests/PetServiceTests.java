package com.glamasw.petitamirestapi.UnitTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.glamasw.petitamirestapi.dtos.PetDTO;
import com.glamasw.petitamirestapi.services.PetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
public class PetServiceTests {

    @Autowired
    PetService petService;

    @Test
    @DisplayName("PetService save() method test: No contact mediums provided")
    public void saveNoContactMediums() throws Exception {
        //Entities init
        PetDTO petDTO = new PetDTO();
        //Atributes setting
        petDTO.setPetName("Chocoperro");
        petDTO.setOwnerName("Cochoperro Owner");
        petDTO.setOwnerDni(49784584);
        petDTO.setContactMediumDTOs(new ArrayList<>());
        //Persist petDTO
        try {
            petDTO = petService.save(petDTO);
        } catch (Exception e) {
            throw new Exception();
        }
        assertTrue(petDTO.getContactMediumDTOs().isEmpty());
    }


}
