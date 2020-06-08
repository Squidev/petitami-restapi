package com.glamasw.petitamirestapi.IntegrationTests;

import com.glamasw.petitamirestapi.controllers.PetController;
import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import net.minidev.json.JSONUtil;
import org.apache.tomcat.util.json.JSONParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest //Tells Spring Boot to look for a main configuration class (one with @SpringBootApplication, for instance) and use that to start a Spring application context.
@AutoConfigureMockMvc
@Execution(ExecutionMode.SAME_THREAD)
public class APITests {

    @Autowired
    PetController petController;
    @Autowired
    OwnerRepository ownerRepository;
    private static List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();
    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setOwnerEntitiesForDBPopulation() {
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
    private void populateDB () {
        ownerRepository.saveAll(ownerEntitiesForDBPopulation);
    }

    /*---------------------------------------------------GET METHOD-------------------------------------------------------------*/

    @Test
    @DisplayName("Get all Pets - Empty DB - Should return empty JSON array")
    @Transactional
    public void getAllPets_emptyDB_shouldSucceed(TestInfo testInfo) throws Exception {
        MvcResult result =   mvc.perform(get("/api/v1/pet/").contentType(MediaType.APPLICATION_JSON).content(""))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
    }

    @Test
    @DisplayName("Get all Pets - Populated DB - Should return array with one JSON object for each Owner in ownerEntitiesForDBPopulation")
    @Transactional
    public void getAllPets_populatedDB_shouldSucceed(TestInfo testInfo) throws Exception {
        //ARRANGE
        populateDB();
        String stringifiedJSONOfPets = "[";
        for (Owner ownerEntity : ownerEntitiesForDBPopulation) {
            List<Pet> pets = ownerEntity.getPets();
            for (Pet petEntity : pets) {
                stringifiedJSONOfPets += petEntityToJsonString(petEntity);
                //Si no se trata de la última Pet del último Owner agregar una coma
                if (ownerEntitiesForDBPopulation.indexOf(ownerEntity) != ownerEntitiesForDBPopulation.size() - 1) {
                    if (pets.indexOf(petEntity) != pets.size()) {
                        stringifiedJSONOfPets += ",";
                    }
                }
            }
        }
        stringifiedJSONOfPets += "]";

        //Comprobación visual
        System.out.println(stringifiedJSONOfPets);

        //ACT AND ASSERT
        MvcResult result =   mvc.perform(get("/api/v1/pet/").content("").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(stringifiedJSONOfPets))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by UUID - Non existant - Should return BAD_REQUEST error")
    @Transactional
    public void getOnePetByUuid_nonExistant_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        MvcResult result = mvc.perform(get("/api/v1/pet/uuid/asdasd").content("{}").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("{\"message\": \"Error: El UUID ingresado no es válido\"}"))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by UUID - Existant, single Owner, one ContactMedium - Should return single JSON object")
    @Transactional
    public void getOnePetByUuid_existantSingleOwnerOneContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
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
        MvcResult result =   mvc.perform(get("/api/v1/pet/uuid/" + petEntity.getUuid()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(petEntityToJsonString(petEntity)))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by UUID - Existant, single Owner, three ContactMedium - Should return single JSON object")
    @Transactional
    public void getOnePetByUuid_existantSingleOwnerThreeContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
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
        //Creation of three ContactMedium
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setType("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity1);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setType("Facebook");
        contactMediumEntity2.setValue("www.instagram.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity2);

        ContactMedium contactMediumEntity3 = new ContactMedium();
        contactMediumEntity3.setType("Facebook");
        contactMediumEntity3.setValue("2614857948");
        ownerEntity.addContactMedium(contactMediumEntity3);
        //Persistence of Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.clear();
        entityManager.close();
        entityManager.getTransaction().commit();
        System.out.println(petEntity.getUuid());
        //ACT AND ASSERT
        MvcResult result =   mvc.perform(get("/api/v1/pet/uuid/" + petEntity.getUuid()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(petEntityToJsonString(petEntity)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by ID - Non existant - Should return BAD_REQUEST error")
    @Transactional
    public void getOnePetById_nonExistant_shouldReturnBadRequestError(TestInfo testInfo) throws Exception {
        MvcResult result = mvc.perform(get("/api/v1/pet/25000").content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Error: El ID ingresado no es válido\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by ID - Existant, single Owner, single ContactMedium - Should return single JSON object")
    @Transactional
    public void getOnePetById_existantSingleOwnerSingleContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
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
        MvcResult result =   mvc.perform(get("/api/v1/pet/" + petEntity.getId()).contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(petEntityToJsonString(petEntity)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get one Pet by ID - Existant, single Owner, three ContactMedium - Should return single JSON object")
    @Transactional
    public void getOnePetById_existantSingleOwnerThreeContactMedium_shouldReturnSingleJsonObject(TestInfo testInfo) throws Exception {
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
        //Creation of three ContactMedium
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setType("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity1);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setType("Facebook");
        contactMediumEntity2.setValue("www.instagram.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity2);

        ContactMedium contactMediumEntity3 = new ContactMedium();
        contactMediumEntity3.setType("Facebook");
        contactMediumEntity3.setValue("2614857948");
        ownerEntity.addContactMedium(contactMediumEntity3);
        //Persistence of Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.clear();
        entityManager.close();
        entityManager.getTransaction().commit();
        System.out.println(petEntity.getUuid());
        //ACT AND ASSERT
        MvcResult result =   mvc.perform(get("/api/v1/pet/" + petEntity.getId()).contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(petEntityToJsonString(petEntity)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    /*---------------------------------------------------POST METHOD-------------------------------------------------------------*/

    @Test
    @DisplayName("Post Pet - New Owner, single ContactMedium - Should return JSON including generated IDs")
    @Transactional
    public void postPet_newOwnerSingleContactMedium_shouldReturnJsonIncludingGeneratedIds() throws Exception {
        //ARRANGE
        //JSON formated string to use in assertion
        String petJSON =   "{\"petId\":0," +
                            "\"petUuid\":null," +
                            "\"petName\":\"Fluffy\"," +
                            "\"petPhoto\":null," +
                            "\"petDescription\":\"Good boy\"," +
                            "\"ownerId\":0," +
                            "\"ownerDni\":48571335," +
                            "\"ownerName\":\"Fluffy Owner\"," +
                            "\"contactMediumDTOs\":[{\"id\":0," +
                                                    "\"type\":\"Facebook\"," +
                                                    "\"value\":\"www.facebook.com/FluffyOwner\"" +
                                                    "}]" +
                            "}";

        int nextOwnerIdAfterDbPopulation = ownerEntitiesForDBPopulation.size() + 1;
        int nextPetIdAfterDbPopulation = ownerEntitiesForDBPopulation.size()*ownerEntitiesForDBPopulation.get(0).getPets().size() + 1;
        int nextContactMediumIdAfterDbPopulation = ownerEntitiesForDBPopulation.size()*ownerEntitiesForDBPopulation.get(0).getContactMediums().size() + 1;

        //ACT AND ASSERT
        //Find out how to check que devuelva un id distinto de 0
        MvcResult result =   mvc.perform(post("/api/v1/pet/").contentType(MediaType.APPLICATION_JSON).content(petJSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(   "{\"petId\":" + nextPetIdAfterDbPopulation + "," +
                                                                        "\"petUuid\":null," +
                                                                        "\"petName\":\"Fluffy\"," +
                                                                        "\"petPhoto\":null," +
                                                                        "\"petDescription\":\"Good boy\"," +
                                                                        "\"ownerId\":" + nextOwnerIdAfterDbPopulation + "," +
                                                                        "\"ownerDni\":48571335," +
                                                                        "\"ownerName\":\"Fluffy Owner\"," +
                                                                        "\"contactMediumDTOs\":[{\"id\":" + nextContactMediumIdAfterDbPopulation + "," +
                                                                                                "\"type\":\"Facebook\"," +
                                                                                                "\"value\":\"www.facebook.com/FluffyOwner\"" +
                                                                                                "}]" +
                                                                        "}"))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
    }

    @Test
    @DisplayName("Post Pet - Existing Owner, single ContactMedium - Should return JSON including generated IDs")
    @Transactional
    public void postPet_existingOwnerSingleContactMedium_shouldReturnJsonIncludingGeneratedIds() throws Exception {
        //ARRANGE
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(48615448);
        ownerEntity.setName("Biggie Owner");
        //Creation of Pet
        Pet petEntity = new Pet();
        petEntity.setName("Biggie");
        petEntity.setPhoto(null);
        petEntity.setDescription("Bad boy");
        ownerEntity.addPet(petEntity);
        //Creation of ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Instagram");
        contactMediumEntity.setValue("www.facebook.com/BiggieOwner");
        ownerEntity.addContactMedium(contactMediumEntity);
        //Persistence of Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.clear();
        entityManager.close();
        entityManager.getTransaction().commit();

        //JSON formated string to use in assertion
        String petJSON =    "{\"petId\":0," +
                            "\"petUuid\":null," +
                            "\"petName\":\"Fluffy\"," +
                            "\"petPhoto\":null," +
                            "\"petDescription\":\"Good boy\"," +
                            "\"ownerId\":" + ownerEntity.getId() + "," +
                            "\"ownerDni\":" + ownerEntity.getDni() + "," +
                            "\"ownerName\":\"" + ownerEntity.getName() + "\"," +
                            "\"contactMediumDTOs\":[{\"id\":" + contactMediumEntity.getId() + "," +
                                                    "\"type\":\"Instagram\"," +
                                                    "\"value\":\"www.instagram.com/BiggieOwner\"" +
                                                    "}," +
                                                    "{\"id\":" + (contactMediumEntity.getId()+1) + "," +
                                                    "\"type\":\"Facebook\"," +
                                                    "\"value\":\"www.facebook.com/FluffyOwner\"" +
                                                    "}]";

        int nextOwnerIdAfterDbPopulation = ownerEntitiesForDBPopulation.size() + 1;
        int nextPetIdAfterDbPopulation = ownerEntitiesForDBPopulation.size()*ownerEntitiesForDBPopulation.get(0).getPets().size() + 1;
        int nextContactMediumIdAfterDbPopulation = ownerEntitiesForDBPopulation.size()*ownerEntitiesForDBPopulation.get(0).getContactMediums().size() + 1;

        //ACT AND ASSERT
        //Find out how to check que devuelva un id distinto de 0
        MvcResult result =   mvc.perform(post("/api/v1/pet/").contentType(MediaType.APPLICATION_JSON).content(petJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(   "{\"petId\":" + nextPetIdAfterDbPopulation + "," +
                        "\"petUuid\":null," +
                        "\"petName\":\"Fluffy\"," +
                        "\"petPhoto\":null," +
                        "\"petDescription\":\"Good boy\"," +
                        "\"ownerId\":" + nextOwnerIdAfterDbPopulation + "," +
                        "\"ownerDni\":48571335," +
                        "\"ownerName\":\"Fluffy Owner\"," +
                        "\"contactMediumDTOs\":[{\"id\":" + nextContactMediumIdAfterDbPopulation + "," +
                        "\"type\":\"Facebook\"," +
                        "\"value\":\"www.facebook.com/FluffyOwner\"" +
                        "}]" +
                        "}"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }



    //---------------------------------------------------------------
    @DisplayName("Save Pet, 5 times - Single Owner, single ContactMedium")
    @RepeatedTest(value = 5, name = "{displayName} ---> Dog {currentRepetition}")
    public void saveDogTest(RepetitionInfo repetitionInfo) throws Exception {

    }

    private String petEntityToJsonString(Pet petEntity) {
        String stringifiedJsonOfPet =  "{\"petId\":" + petEntity.getId() + "," +
                "\"petUuid\":\"" + petEntity.getUuid() + "\"," +
                "\"petName\":\"" + petEntity.getName() + "\"," +
                "\"petPhoto\":" + petEntity.getPhoto() + "," +
                "\"petDescription\":\"" + petEntity.getDescription() + "\"," +
                "\"ownerId\":" + petEntity.getOwner().getId() + "," +
                "\"ownerDni\":" + petEntity.getOwner().getDni() + "," +
                "\"ownerName\":\"" + petEntity.getOwner().getName() + "\"," +
                "\"contactMediumDTOs\":" + "[";
        List<ContactMedium> contactMediums = petEntity.getOwner().getContactMediums();
        for (ContactMedium contactMedium : contactMediums) {
            stringifiedJsonOfPet += "{\"id\":" + contactMedium.getId() + "," +
                    "\"type\":\"" + contactMedium.getType() + "\"," +
                    "\"value\":\"" + contactMedium.getValue() + "\"}";
            //Si no se trata del último ContactMedium del Owner agregar una coma
            if (contactMediums.indexOf(contactMedium) != contactMediums.size() - 1) {
                stringifiedJsonOfPet += ",";
            }
        }
        stringifiedJsonOfPet += "]}";
        return stringifiedJsonOfPet;
    }

    String contactMediumsToString(List<ContactMedium> contactMediums) {
        //Se abre el array
        String stringifiedContactMediums = "[";
        //Se convierte cada ContactMedium a su formato JSON y se añade al String.
        for (ContactMedium cm: contactMediums) {
            stringifiedContactMediums = stringifiedContactMediums + "{contactMediumName: "+cm.getType()+", contactMediumValue: "+cm.getValue()+"}";
            //Si no se llegó al final del array, se agrega una "," para separar los JSON.
            if (contactMediums.indexOf(cm)!=contactMediums.toArray().length-1){
                stringifiedContactMediums += ", ";
            }
        }
        //Se cierra el array
        stringifiedContactMediums += "]";
        return stringifiedContactMediums;
    }

    public void updateDogTest(TestInfo testInfo) {

    }

    public void deleteDogTest(TestInfo testInfo) {

    }

    /*public void deleteDogTest(TestInfo testInfo) {
        String[] dnaArray = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        MvcResult result = mvc.perform(post("/mutant/").content("{\"dna\":"+asJsonString(dnaArray)+"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("OK"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        System.out.println("JSON send: {\"dna\":"+asJsonString(dnaArray)+"} Api Return ===> "+content);
    }*/
}