package com.glamasw.petitamirestapi.IntegrationTests.APITests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class APIOwnerTests {

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    MockMvc mockMvc;
    private List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();
    @Autowired
    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setOwnerEntitiesForDBPopulation() {
        ownerEntitiesForDBPopulation.clear();
        for (int i = 1; i <= 5; i++) {
            //Creation of Owner
            Owner ownerEntity = new Owner();
            ownerEntity.setDni(54149870+i);
            ownerEntity.setName("Chocoperro" + i + " Owner");
            //Creation of Pet
            Pet petEntity = new Pet();
            petEntity.setName("Chocoperro " + i);
            petEntity.setDescription("Description " + i);
            ownerEntity.addPet(petEntity);
            //Creation of 3 ContactMedium
            ContactMedium contactMedium1 = new ContactMedium();
            contactMedium1.setType("Facebook");
            contactMedium1.setValue("www.facebook.com/Chocoperro" + i + "Owner");
            ownerEntity.addContactMedium(contactMedium1);

            ContactMedium contactMedium2 = new ContactMedium();
            contactMedium2.setType("Instagram");
            contactMedium2.setValue("www.instagram.com/Chocoperro" + i + "Owner");
            ownerEntity.addContactMedium(contactMedium2);

            ContactMedium contactMedium3 = new ContactMedium();
            contactMedium3.setType("Telefono");
            contactMedium3.setValue("261487510" + i);
            ownerEntity.addContactMedium(contactMedium3);
            //Addition of Owner to the list
            ownerEntitiesForDBPopulation.add(ownerEntity);
        }
    }

    @BeforeEach
    void dbCleanUp() {
        ownerRepository.deleteAll();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("ALTER TABLE pet AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE contact_medium AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE owner AUTO_INCREMENT = 1").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private void populateDB () {
        ownerRepository.saveAll(ownerEntitiesForDBPopulation);
    }

    /*--------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------GET REQUEST------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------*/

    @Test
    @DisplayName("Get all Owners - Empty DB - Should return empty JSON array")
    public void getAllOwners_emptyDB_shouldReturnEmptyJsonArray(TestInfo testInfo) throws Exception {
        //ARRANGE
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/owner/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("[]"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get all Owners - Populated DB - Should return array with one JSON object for each Owner in ownerEntitiesForDBPopulation")
    public void getAllOwners_populatedDB_shouldReturnJsonArrayOfOwners(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        String jsonPayloadOfOwners = "[";
        for (Owner ownerEntity : ownerEntitiesForDBPopulation) {
            List<Pet> pets = ownerEntity.getPets();
            jsonPayloadOfOwners += ownerEntityToJsonString(ownerEntity);
            //Si no se trata del último Owner agregar una coma
            if (ownerEntitiesForDBPopulation.indexOf(ownerEntity) != ownerEntitiesForDBPopulation.size() - 1) {
                jsonPayloadOfOwners += ",";
            }
        }
        jsonPayloadOfOwners += "]";

        //Comprobación visual
        System.out.println(jsonPayloadOfOwners);

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/owner/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json(jsonPayloadOfOwners))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get one Owner by DNI - Nonexistent - Should return BAD_REQUEST error")
    public void getOneOwnerByDni_nonExistent_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/owner/dni/0")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isBadRequest())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"Error: El DNI ingresado no es válido\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get one Owner by DNI - Existent, single Pet, single ContactMedium - Should return single JSON object")
    public void getOneOwnerByDni_existentSinglePetSingleContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(54484154);
        ownerEntity.setName("Fluffy Owner");
        //Creation of Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        ownerEntity.addPet(petEntity);
        //Creation of ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity);
        //Persistence of Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.clear();
        entityManager.close();
        entityManager.getTransaction().commit();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/owner/dni/" + ownerEntity.getDni())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json(ownerEntityToJsonString(ownerEntity)))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get one Owner by ID - Nonexistent - Should return BAD_REQUEST error")
    public void getOneOwnerById_nonExistent_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/owner/0")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isBadRequest())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"Error: El ID ingresado no es válido\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get one Owner by ID - Existent, single Pet, single ContactMedium - Should return single JSON object")
    public void getOneOwnerById_existentSinglePetSingleContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(54484154);
        ownerEntity.setName("Fluffy Owner");
        //Creation of Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        ownerEntity.addPet(petEntity);
        //Creation of ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity);
        //Persistence of Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.clear();
        entityManager.close();
        entityManager.getTransaction().commit();
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/owner/" + ownerEntity.getId())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json(ownerEntityToJsonString(ownerEntity)))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------POST REQUEST------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/

    @Test
    @DisplayName("Post Owner - Should return JSON including generated IDs")
    public void postOwner_shouldReturnJsonIncludingGeneratedId() throws Exception {
        //ARRANGE
        populateDB();
        //JSON payload
        String jsonPayloadOfOwner = "{\"id\":0," +
                                     "\"dni\":44512485," +
                                     "\"name\":\"Owner\"}";
        //Next Owner id after DB population
        int nextOwnerId = ownerEntitiesForDBPopulation.size() + 1;

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(post("/api/v1/owner/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonPayloadOfOwner))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json( "{\"id\":" + nextOwnerId + "," +
                                                                        "\"dni\":44512485," +
                                                                        "\"name\":\"Owner\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------PUT REQUEST-------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/

    @Test
    @DisplayName("Update Owner - Valid id - Should return updated JSON of Owner")
    public void updateOwner_validId_shouldReturnUpdatedJsonOfOwner(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        //Creation of existing Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(54484154);
        ownerEntity.setName("Fluffy Owner");
        //Creation of existing Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        ownerEntity.addPet(petEntity);
        //Creation of existing ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity);
        //Persistence of existing Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.clear();
        entityManager.close();
        entityManager.getTransaction().commit();
        //JSON payload
        String jsonPayloadOfOwner = "{\"id\":0," +
                                     "\"dni\":" + (ownerEntity.getDni() + 1) + "," +
                                     "\"name\":\"Updated Owner\"}";
        //ACT AND ASSERT
        System.out.println(petEntity.getId());
        MvcResult result = mockMvc.perform(put("/api/v1/owner/" + ownerEntity.getId())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonPayloadOfOwner))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"id\":" + ownerEntity.getId() + "," +
                                                                       "\"dni\":" + (ownerEntity.getDni() + 1) + "," +
                                                                       "\"name\":\"Updated Owner\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Update Owner - Invalid id - Should return BAD_REQUEST error")
    public void updateOwner_invalidId_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        //JSON payload
        String jsonPayloadOfOwner = "{\"id\":0," +
                                     "\"dni\":54484154," +
                                     "\"name\":\"Updated Owner\"}";
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(put("/api/v1/owner/0")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonPayloadOfOwner))
                                  .andExpect(status().isBadRequest())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------DELETE REQUEST-----------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/

    @Test
    @DisplayName("Delete Owner - Valid id - Should return success message")
    public void deleteOwner_validId_shouldReturnSuccessMessage(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        //Creation of existing Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(54484154);
        ownerEntity.setName("Fluffy Owner");
        //Creation of existing Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        ownerEntity.addPet(petEntity);
        //Creation of existing ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity);
        //Persistence of existing Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.clear();
        entityManager.close();
        entityManager.getTransaction().commit();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(delete("/api/v1/owner/" + ownerEntity.getId())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"El registro ha sido eliminado con éxito\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Delete Owner - Invalid id - Should return BAD_REQUEST error")
    public void deleteOwner_invalidId_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(delete("/api/v1/owner/0")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isBadRequest())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }



    private String ownerEntityToJsonString(Owner ownerEntity) {
        String stringifiedJsonOfOwner =    "{\"id\":" + ownerEntity.getId() + "," +
                                            "\"dni\":" + ownerEntity.getDni() + "," +
                                            "\"name\":\"" + ownerEntity.getName() + "\"}";
        return stringifiedJsonOfOwner;
    }
}
