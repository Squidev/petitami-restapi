package com.glamasw.petitamirestapi.IntegrationTests.APITests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.repositories.ContactMediumRepository;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
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
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class APIContactMediumTests {

    @Autowired
    ContactMediumRepository contactMediumRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    EntityManagerFactory entityManagerFactory;
    @Autowired
    MockMvc mockMvc;
    private List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();

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
        entityManager.createNativeQuery("ALTER SEQUENCE pet_id_seq RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE contact_medium_id_seq RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE owner_id_seq RESTART WITH 1").executeUpdate();
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
    @DisplayName("Get all ContactMediums - Empty DB - Should return empty JSON array")
    public void getAllContactMediums_emptyDB_shouldReturnEmptyJsonArray(TestInfo testInfo) throws Exception {
        //ARRANGE

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/contactmedium/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())                                         //» Testing the HTTP response Status Code
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))       //» Testing the Media Type
                                  .andExpect(content().json("[]"))                          //» Testing the Payload (JSON, XML)
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get all ContactMediums - Populated DB - Should return array with one JSON object for each ContactMedium in ownerEntitiesForDBPopulation")
    public void getAllContactMediums_populatedDB_shouldReturnJsonArrayOfContactMediums(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        String jsonPayloadOfContactMediums = "[";
        for (Owner ownerEntity : ownerEntitiesForDBPopulation) {
            List<ContactMedium> contactMediums = ownerEntity.getContactMediums();
            for (ContactMedium contactMediumEntity : contactMediums) {
                jsonPayloadOfContactMediums += contactMediumEntityToJsonString(contactMediumEntity);
                //Si se trata del último ContactMedium del último Owner no se agrega la coma
                if (ownerEntitiesForDBPopulation.indexOf(ownerEntity) == ownerEntitiesForDBPopulation.size() - 1
                    && contactMediums.indexOf(contactMediumEntity) == contactMediums.size()-1) {
                        break;
                    }
                jsonPayloadOfContactMediums += ",";
            }
        }
        jsonPayloadOfContactMediums += "]";
        //Comprobación visual
        System.out.println(jsonPayloadOfContactMediums);

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/contactmedium/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json(jsonPayloadOfContactMediums))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get one ContactMedium by ID - Nonexistent - Should return BAD_REQUEST error")
    public void getOneContactMediumById_nonExistent_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/contactmedium/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Error: El ID ingresado no es válido\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get one ContactMedium by ID - Existing single Owner, single Pet - Should return single JSON object")
    public void getOneContactMediumById_existentSingleOwnerSinglePet_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
        //ARRANGE
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
        System.out.println(petEntity.getUuid());

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/contactmedium/" + contactMediumEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(contactMediumEntityToJsonString(contactMediumEntity)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get contact mediums by owner id - Existing Owner related to no ContactMedium and single Pet - Should return empty list of ContactMedium")
    public void getContactMediumsByOwnerId_existingOwnerRelatedToNoContactMediumAndSinglePet_shouldReturnEmptyListOfContactMedium() throws Exception {
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
        //Persistence of existing Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/contactmedium/owner/" + ownerEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get contact mediums by owner id - Existing Owner related to three ContactMedium and single Pet - Should return list of existing three ContactMedium")
    public void getContactMediumsByOwnerId_existingOwnerRelatedToThreeContactMediumAndSinglePet_shouldReturnListOfExistingThreeContactMedium() throws Exception {
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
        //Creation of three existing ContactMedium
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setType("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity1);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setType("Instagram");
        contactMediumEntity2.setValue("www.instagram.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity2);

        ContactMedium contactMediumEntity3 = new ContactMedium();
        contactMediumEntity3.setType("Tel");
        contactMediumEntity3.setValue("0261854862");
        ownerEntity.addContactMedium(contactMediumEntity3);
        //Persistence of existing Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();
        //JSON payload
        String jsonPayloadOfContactMediums = "[";
        List<ContactMedium> contactMediums = ownerEntity.getContactMediums();
        for (ContactMedium contactMediumEntity: contactMediums) {
            jsonPayloadOfContactMediums += contactMediumEntityToJsonString(contactMediumEntity);
            //Si se trata de la última Pet no se agrega la coma
            if (contactMediums.indexOf(contactMediumEntity) == contactMediums.size() - 1) {
                break;
            }
            jsonPayloadOfContactMediums += ",";
        }
        jsonPayloadOfContactMediums += "]";
        //Comprobación visual
        System.out.println(jsonPayloadOfContactMediums);

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/contactmedium/owner/" + ownerEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonPayloadOfContactMediums))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get contact mediums by owner id - Non existing Owner - Should return BAD_REQUEST error")
    public void getContactMediumsByOwnerId_nonExistingOwner_shouldReturnBadRequestError() throws Exception {
        //ARRANGE
        populateDB();
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/contactmedium/owner/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------POST REQUEST------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/

    @Test
    @DisplayName("Post ContactMedium - Nonexistent Owner - Should return BAD_REQUEST error")
    public void postContactMedium_nonExistentOwner_shouldReturnBadRequestError() throws Exception {
        //ARRANGE
        populateDB();
        //JSON payload
        String jsonPayloadOfContactMedium =  "{\"id\":0," +
                                              "\"type\":\"Facebook\"," +
                                              "\"value\":\"www.facebook.com/FluffyOwner\"," +
                                              "\"ownerId\":0}";

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(post("/api/v1/contactmedium/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonPayloadOfContactMedium))
                                  .andExpect(status().isBadRequest())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Post ContactMedium - Existing Owner, single Pet - Should return JSON including generated ID")
    public void postContactMedium_existingOwnerSinglePet_shouldReturnJsonIncludingGeneratedId() throws Exception {
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
        String jsonPayloadOfContactMedium =  "{\"id\":0," +
                                              "\"type\":\"Instagram\"," +
                                              "\"value\":\"www.instagram.com/FluffyOwner\"," +
                                              "\"ownerId\":" + ownerEntity.getId() + "}";
        //Next ContactMedium id after DB population and existing Owner persistence
        int nextContactMediumId = ownerEntitiesForDBPopulation.size()*ownerEntitiesForDBPopulation.get(0).getContactMediums().size() +
                ownerEntity.getContactMediums().size() +
                1;

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(post("/api/v1/contactmedium/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonPayloadOfContactMedium))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"id\":" + nextContactMediumId + "," +
                                                                       "\"type\":\"Instagram\"," +
                                                                       "\"value\":\"www.instagram.com/FluffyOwner\"," +
                                                                       "\"ownerId\":" + ownerEntity.getId() + "}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------PUT REQUEST-------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/

    @Test
    @DisplayName("Update ContactMedium - Valid id - Should return updated JSON of ContactMedium")
    public void updateContactMedium_validId_shouldReturnUpdatedJsonOfPet(TestInfo testInfo) throws Exception {
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
        String jsonPayloadOfContactMedium = "{\"id\":0," +
                                             "\"type\":\"Instagram\"," +
                                             "\"value\":\"www.instagram.com/FluffyOwner\"," +
                                             "\"ownerId\":" + ownerEntity.getId() + "}";
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(put("/api/v1/contactmedium/" + contactMediumEntity.getId())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonPayloadOfContactMedium))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"id\":" + contactMediumEntity.getId() + "," +
                                                                       "\"type\":\"Instagram\"," +
                                                                       "\"value\":\"www.instagram.com/FluffyOwner\"," +
                                                                       "\"ownerId\":" + ownerEntity.getId() + "}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Update Pet - Invalid id - Should return BAD_REQUEST error")
    public void updatePet_invalidId_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
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
        String jsonPayloadOfContactMedium = "{\"id\":0," +
                                             "\"type\":\"Instagram\"," +
                                             "\"value\":\"www.instagram.com/FluffyOwner\"," +
                                             "\"ownerId\":" + ownerEntity.getId() + "}";
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(put("/api/v1/contactmedium/0")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonPayloadOfContactMedium))
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
    @DisplayName("Delete ContactMedium - Valid id - Should return success message")
    public void deleteContactMedium_validId_shouldReturnSuccessMessage(TestInfo testInfo) throws Exception {
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
        MvcResult result = mockMvc.perform(delete("/api/v1/contactmedium/" + contactMediumEntity.getId())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"El registro ha sido eliminado con éxito\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Delete ContactMedium - Invalid id - Should return BAD_REQUEST error")
    public void deleteContactMedium_invalidId_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(delete("/api/v1/contactmedium/0")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isBadRequest())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    private String contactMediumEntityToJsonString(ContactMedium contactMediumEntity) {
        String stringifiedJsonOfContactMedium =    "{\"id\":" + contactMediumEntity.getId() + "," +
                                                    "\"type\":\"" + contactMediumEntity.getType() + "\"," +
                                                    "\"value\":\"" + contactMediumEntity.getValue() + "\"," +
                                                    "\"ownerId\":" + contactMediumEntity.getOwner().getId() + "}";
        return stringifiedJsonOfContactMedium;
    }
}
