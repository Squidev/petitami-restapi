package com.glamasw.petitamirestapi.IntegrationTests.APITests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import com.glamasw.petitamirestapi.repositories.PetRepository;
import com.sun.nio.sctp.HandlerResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest //Tells Spring Boot to look for a main configuration class (one with @SpringBootApplication, for instance) and use that to start a Spring application context.
@AutoConfigureMockMvc
@Execution(ExecutionMode.SAME_THREAD)
public class APIPetTests {

    @Autowired
    OwnerRepository ownerRepository;
    private List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();
    @Autowired
    EntityManagerFactory entityManagerFactory;
    @Autowired
    PetRepository petRepository;

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("Get all Pets - Empty DB - Should return empty JSON array")
    public void getAllPets_emptyDB_shouldReturnEmptyJsonArray(TestInfo testInfo) throws Exception {
        /*When testing a REST resource, there are usually a few orthogonal responsibilities the tests should focus on:
        » Testing the HTTP response Status Code
        » Testing the Media Type
        » Testing the Payload (JSON, XML)
        Each test should only focus on a single responsibility and include a single assertion. Focusing on a clear separation always has benefits, but when doing this
        kind of black box testing is even more important, as the general tendency is to write complex test scenarios in the very beginning.

                Another important aspect of the integration tests is adherence to the Single Level of Abstraction Principle – the logic within a test should be written at a high level. Details such as creating the request, sending the HTTP request to the server, dealing with IO, etc should not be done inline but via utility methods*/

        //ARRANGE

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/pet/")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(""))
                              .andExpect(status().isOk())                                         //» Testing the HTTP response Status Code
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))       //» Testing the Media Type
                              .andExpect(content().json("[]"))                          //» Testing the Payload (JSON, XML)
                              .andDo(MockMvcResultHandlers.print())
                              .andReturn();
    }

    @Test
    @DisplayName("Get all Pets - Populated DB - Should return array with one JSON object for each Pet in ownerEntitiesForDBPopulation")
    public void getAllPets_populatedDB_shouldReturnJsonArrayOfPets(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        String jsonPayloadOfPets = "[";
        for (Owner ownerEntity : ownerEntitiesForDBPopulation) {
            List<Pet> pets = ownerEntity.getPets();
            for (Pet petEntity : pets) {
                jsonPayloadOfPets += petEntityToJsonString(petEntity);
                //Si se trata de la última Pet del último Owner  no se agrega la coma
                if (ownerEntitiesForDBPopulation.indexOf(ownerEntity) == ownerEntitiesForDBPopulation.size() - 1
                    && pets.indexOf(petEntity) == pets.size() - 1) {
                        break;
                    }
                jsonPayloadOfPets += ",";
            }
        }
        jsonPayloadOfPets += "]";
        //Comprobación visual
        System.out.println(jsonPayloadOfPets);

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/pet/")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(""))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(jsonPayloadOfPets))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by UUID - Nonexistent - Should return BAD_REQUEST error")
    public void getOnePetByUuid_nonExistent_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/pet/uuid/asdasd")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("{\"message\": \"Error: El UUID ingresado no es válido\"}"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by UUID - Existent single Owner, one ContactMedium - Should return single JSON object")
    public void getOnePetByUuid_existentSingleOwnerOneContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
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
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();
        System.out.println(petEntity.getUuid());
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/pet/uuid/" + petEntity.getUuid())
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content("{}"))
                              .andExpect(status().isOk())
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                              .andExpect(content().json(petEntityToJsonString(petEntity)))
                              .andDo(MockMvcResultHandlers.print())
                              .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by ID - Nonexistent - Should return BAD_REQUEST error")
    public void getOnePetById_nonExistent_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/pet/25000")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(""))
                              .andExpect(status().isBadRequest())
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                              .andExpect(content().json("{\"message\": \"Error: El ID ingresado no es válido\"}"))
                              .andDo(MockMvcResultHandlers.print())
                              .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by ID - Existent single Owner, single ContactMedium - Should return single JSON object")
    public void getOnePetById_existentSingleOwnerSingleContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
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
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();

        System.out.println(petEntity.getUuid());
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/pet/" + petEntity.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(""))
                              .andExpect(status().isOk())
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                              .andExpect(content().json(petEntityToJsonString(petEntity)))
                              .andDo(MockMvcResultHandlers.print())
                              .andReturn();
    }

    @Test
    @DisplayName("Get pets by owner id - Existing Owner related to no Pet and single ContactMedium - Should return empty list of Pets")
    public void getPetsByOwnerId_existingOwnerRelatedToNoPetAndSingleContactMedium_shouldReturnEmptyListOfPets() throws Exception {
        //ARRANGE
        populateDB();
        //Creation of existing Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(54484154);
        ownerEntity.setName("Fluffy Owner");
        //Creation of existing ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity);
        //Persistence of existing Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/pet/owner/" + ownerEntity.getId())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(content().json("[]"))
                                  .andDo(MockMvcResultHandlers.print())
                                  .andReturn();
    }

    @Test
    @DisplayName("Get pets by owner id - Existing Owner related to three Pet and single ContactMedium - Should return list of existing three Pets")
    public void getPetsByOwnerId_existingOwnerRelatedToThreePetAndSingleContactMedium_shouldReturnListOfExistingThreePets() throws Exception {
        //ARRANGE
        populateDB();
        //Creation of existing Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(54484154);
        ownerEntity.setName("Fluffy Owner");
        //Creation of three existing Pet
        Pet petEntity1 = new Pet();
        petEntity1.setName("Fluffy");
        petEntity1.setDescription("Good boy");
        ownerEntity.addPet(petEntity1);

        Pet petEntity2 = new Pet();
        petEntity2.setName("Biggie");
        petEntity2.setDescription("Bad boy");
        ownerEntity.addPet(petEntity2);

        Pet petEntity3 = new Pet();
        petEntity3.setName("Fatty");
        petEntity3.setDescription("Rubenesque boy");
        ownerEntity.addPet(petEntity3);
        //Creation of existing ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity);
        //Persistence of existing Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();
        //JSON payload
        String jsonPayloadOfPets = "[";
        List<Pet> pets = ownerEntity.getPets();
        for (Pet petEntity: pets) {
            jsonPayloadOfPets += petEntityToJsonString(petEntity);
            //Si se trata de la última Pet no se agrega la coma
            if (pets.indexOf(petEntity) == pets.size() - 1) {
                break;
            }
            jsonPayloadOfPets += ",";
        }
        jsonPayloadOfPets += "]";
        //Comprobación visual
        System.out.println(jsonPayloadOfPets);

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/pet/owner/" + ownerEntity.getId())
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(""))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(jsonPayloadOfPets))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
    }

    @Test
    @DisplayName("Get pets by owner id - Non existing Owner - Should return BAD_REQUEST error")
    public void getPetsByOwnerId_nonExistingOwner_shouldReturnBadRequestError() throws Exception {
        //ARRANGE
        populateDB();
        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(get("/api/v1/pet/owner/0")
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
    @DisplayName("Post Pet - Nonexistent Owner - Should return BAD_REQUEST error")
    public void postPet_nonExistentOwner_shouldReturnBadRequestError() throws Exception {
        //ARRANGE
        populateDB();
        //JSON payload
        String jsonPayloadOfPet =  "{\"id\":0," +
                                    "\"uuid\":null," +
                                    "\"name\":\"Fluffy\"," +
                                    "\"photo\":null," +
                                    "\"description\":\"Good boy\"," +
                                    "\"ownerId\":0}";

        //ACT AND ASSERT
        //Como desconocemos el UUID que se devolverá, ya que se genera en el back-end luego de enviar el HTTP request, usamos jsonPath() para poder acceder
        // individualmente a los miembros del payload y verificar que el UUID devuelto sea de tipo String.
        //(Find out how to check que devuelva un id>0)
        MvcResult result = mockMvc.perform(post("/api/v1/pet/")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(jsonPayloadOfPet))
                              .andExpect(status().isBadRequest())
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                              .andExpect(content().json("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}"))
                              .andDo(MockMvcResultHandlers.print())
                              .andReturn();
    }

    @Test
    @DisplayName("Post Pet - Existing Owner, single ContactMedium - Should return JSON including generated IDs")
    public void postPet_existingOwnerSingleContactMedium_shouldReturnJsonIncludingGeneratedIds() throws Exception {
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
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();
        //JSON payload
        String jsonPayloadOfPet =  "{\"id\":0," +
                                    "\"uuid\":null," +
                                    "\"name\":\"Fluffy\"," +
                                    "\"photo\":\"\"," +
                                    "\"description\":\"Good boy\"," +
                                    "\"ownerId\":" + ownerEntity.getId() + "}";
        //Next Pet id after DB population and existing Owner persistence
        int nextPetId = ownerEntitiesForDBPopulation.size() * (ownerEntitiesForDBPopulation.get(0).getPets().size())
                        +ownerEntity.getPets().size()
                        + 1;

        //ACT AND ASSERT
        //Como desconocemos el UUID que se devolverá, ya que se genera en el back-end luego de enviar el HTTP request, usamos jsonPath() para poder acceder
        // individualmente a los miembros del payload y verificar que el UUID devuelto sea de tipo String.
        //(Find out how to check que devuelva un id>0)
        MvcResult result = mockMvc.perform(post("/api/v1/pet/")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(jsonPayloadOfPet))
                              .andExpect(status().isOk())
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                              .andExpect(jsonPath("$.id").value(nextPetId))
                              .andExpect(jsonPath("$.uuid").isString())
                              .andExpect(jsonPath("$.name").value("Fluffy"))
                              .andExpect(jsonPath("$.photo").value(""))
                              .andExpect(jsonPath("$.description").value("Good boy"))
                              .andExpect(jsonPath("$.ownerId").value(ownerEntity.getId()))
                              .andDo(MockMvcResultHandlers.print())
                              .andReturn();
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------PUT REQUEST-------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/

    @Test
    @DisplayName("Update Pet - Valid id - Should return updated JSON of Pet")
    public void updatePet_validId_shouldReturnUpdatedJsonOfPet(TestInfo testInfo) throws Exception {
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
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();
        //JSON payload
        String jsonPayloadOfPet =  "{\"id\":0," +
                                    "\"uuid\":\"" + petEntity.getUuid() + "\"," +
                                    "\"name\":\"Biggie\"," +
                                    "\"photo\":\"\"," +
                                    "\"description\":\"Bad boy\"," +
                                    "\"ownerId\":" + ownerEntity.getId() + "}";
        //ACT AND ASSERT
        System.out.println(petEntity.getId());
        MvcResult result = mockMvc.perform(put("/api/v1/pet/" + petEntity.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(jsonPayloadOfPet))
                              .andExpect(status().isOk())
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                              .andExpect(content().json( "{\"id\":" + petEntity.getId() + "," +
                                                                    "\"uuid\":\"" + petEntity.getUuid() + "\"," +
                                                                    "\"name\":\"Biggie\"," +
                                                                    "\"photo\":\"\"," +
                                                                    "\"description\":\"Bad boy\"," +
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
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();
        //JSON payload
        String jsonPayloadOfPet =  "{\"id\":0," +
                                    "\"uuid\":\"" + petEntity.getUuid() + "\"," +
                                    "\"name\":\"Biggie\"," +
                                    "\"photo\":null," +
                                    "\"description\":\"Bad boy\"," +
                                    "\"ownerId\":" + ownerEntity.getId() + "}";
        //ACT AND ASSERT
        System.out.println(petEntity.getId());
        MvcResult result = mockMvc.perform(put("/api/v1/pet/0")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(jsonPayloadOfPet))
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
    @DisplayName("Delete Pet - Valid id - Should return success message")
    public void deletePet_validId_shouldReturnSuccessMessage(TestInfo testInfo) throws Exception {
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
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(delete("/api/v1/pet/" + petEntity.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(""))
                              .andExpect(status().isOk())
                              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                              .andExpect(content().json("{\"message\": \"El registro ha sido eliminado con éxito\"}"))
                              .andDo(MockMvcResultHandlers.print())
                              .andReturn();
    }

    @Test
    @DisplayName("Delete Pet - Invalid id - Should return BAD_REQUEST error")
    public void deletePet_invalidId_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();

        //ACT AND ASSERT
        MvcResult result = mockMvc.perform(delete("/api/v1/pet/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


    //---------------------------------------------------------------
    @DisplayName("Save Pet, 5 times - Single Owner, single ContactMedium")
    @RepeatedTest(value = 5, name = "{displayName} ---> Dog {currentRepetition}")
    public void saveDogTest(RepetitionInfo repetitionInfo) throws Exception {

    }

    private String petEntityToJsonString(Pet petEntity) {
        return "{\"id\":" + petEntity.getId() + "," +
                "\"uuid\":\"" + petEntity.getUuid() + "\"," +
                "\"name\":\"" + petEntity.getName() + "\"," +
                "\"photo\":\"" + petEntity.getPhoto() + "\"," +
                "\"description\":\"" + petEntity.getDescription() + "\"," +
                "\"ownerId\":" + petEntity.getOwner().getId() + "}";
    }
}