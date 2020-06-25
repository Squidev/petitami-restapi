package com.glamasw.petitamirestapi.IntegrationTests.RepositoryTests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.repositories.ContactMediumRepository;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ContactMediumRepositoryTests {
    @Autowired
    ContactMediumRepository contactMediumRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    EntityManagerFactory entityManagerFactory;
    @Autowired
    ValidatorFactory validatorFactory;
    private List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();
    @Autowired
    TransactionTemplate transactionTemplate;

    @BeforeEach
    void setOwnerEntitiesForDBPopulation() {
        ownerEntitiesForDBPopulation.clear();
        for (int i = 1; i <= 5; i++) {
            //Creation of Pet
            Pet petEntity = new Pet();
            petEntity.setName("Chocoperro " + i);
            petEntity.setDescription("Description " + i);
            //Creation of Owner
            Owner ownerEntity = new Owner();
            ownerEntity.setDni(54149870+i);
            ownerEntity.setName("Chocoperro" + i + " Owner");
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
    }

    void populateDB() {
        ownerRepository.saveAll(ownerEntitiesForDBPopulation);
    }

    @Test
    @DisplayName("Find all ContactMediums - Empty DB - Should return empty list of ContactMediums")
    void findAllContactMediums_emptyDB_shouldReturnEmptyListOfContactMediums() {
        //ARRANGE
        //ACT
        List<ContactMedium> foundContactMediums = contactMediumRepository.findAll();
        //ASSERT
        assertTrue(foundContactMediums.isEmpty());
    }

    @Test
    @DisplayName("Find all ContactMediums - Populated DB - Should return full list of ContactMediums")
    void findAllContactMediums_populatedDB_shouldReturnFullListOfContactMediums(){
        //ARRANGE
        //Population of DB
        populateDB();
        //Array of all persisted ContactMediums
        List<ContactMedium> persistedContactMediums = new ArrayList<>();
        for (Owner ownerEntity : ownerEntitiesForDBPopulation) {
            persistedContactMediums.addAll(ownerEntity.getContactMediums());
        }
        //ACT
        List<ContactMedium> foundContactMediums = contactMediumRepository.findAll();
        System.out.println(foundContactMediums.size());
        //ASSERT
        assertTrue(foundContactMediums.size() == persistedContactMediums.size());
        assertArrayEquals(foundContactMediums.toArray(), persistedContactMediums.toArray());
    }

    @Test
    @DisplayName("Find ContactMedium - By id - Should succeed")
    void findContactMedium_byId_shouldSucceed() {
        //ARRANGE
        //Population of DB
        populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(48419877);
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
        //ACT AND ASSERT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(contactMediumEntity.getId());
                ContactMedium foundContactMedium = optionalContactMedium.get();
                //ASSERT
                //ContactMedium data coincide
                assertEquals(foundContactMedium, contactMediumEntity);
                //Owner data coincide
                assertEquals(foundContactMedium.getOwner(), ownerEntity);
                //Pet data coincide
                assertArrayEquals(foundContactMedium.getOwner().getPets().toArray(), ownerEntity.getPets().toArray());
            }
        });
    }

    @Test
    @DisplayName("Save ContactMedium - Existing Owner related to single Pet and single ContactMedium - Should succeed")
    void savePet_existingOwnerRelatedToSinglePetAndSingleContactMedium_shouldSucceed() {
        //ARRANGE
        //Population of DB
        populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(48419877);
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
        //Creation of ContactMedium to save
        ContactMedium contactMediumToSave = new ContactMedium();
        contactMediumToSave.setType("Instagram");
        contactMediumToSave.setValue("www.instagram.com/FluffyOwner");

        //ACT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
                Owner foundOwner = optionalOwner.get();
                contactMediumToSave.setOwner(foundOwner);
                contactMediumRepository.save(contactMediumToSave);
            }
        });

        //ASSERT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
                Owner foundOwner = optionalOwner.get();
                assertTrue(contactMediumToSave.getId() != 0);
                assertTrue(contactMediumToSave.getOwner().getId() == ownerEntity.getId());
                assertTrue(foundOwner.getContactMediums().size() ==2);
                assertEquals(foundOwner.getContactMediums().get(1), contactMediumToSave);
            }
        });
    }

    @Test
    @DisplayName("Delete ContactMedium - Existing Owner related to three Pets and three ContactMediums - Should succeed")
    void deleteContactMedium_existingOwnerRelatedToThreePetsAndThreeContactMediums_shouldSucceed() {
        //ARRANGE
        //Population of DB
        populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy, Biggie and Fatty Owner");
        ownerEntity.setDni(48419877);
        //Creation of Pets
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

        //Creation of ContactMediums
        ContactMedium contactMediumEntityToDelete = new ContactMedium();
        contactMediumEntityToDelete.setType("Facebook");
        contactMediumEntityToDelete.setValue("www.facebook.com/FluffyAndBiggieAndFattyOwner");
        ownerEntity.addContactMedium(contactMediumEntityToDelete);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setType("Instagram");
        contactMediumEntity2.setValue("www.instagram.com/FluffyAndBiggieAndFattyOwner");
        ownerEntity.addContactMedium(contactMediumEntity2);

        ContactMedium contactMediumEntity3 = new ContactMedium();
        contactMediumEntity3.setType("Telefono");
        contactMediumEntity3.setValue("2614875579");
        ownerEntity.addContactMedium(contactMediumEntity3);
        //Persistence of Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();

        //ACT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(contactMediumEntityToDelete.getId());
                ContactMedium foundContactMedium = optionalContactMedium.get();
                Owner owner = foundContactMedium.getOwner();
                //Desasociación del ContactMedium
                owner.removeContactMedium(foundContactMedium);
                //Deleteo del ContactMedium
                contactMediumRepository.delete(foundContactMedium);
            }
        });

        //ASSERT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                //El Optional devuelto no incluye un ContactMedium existente
                assertTrue(contactMediumRepository.findById(contactMediumEntityToDelete.getId()).isEmpty());
                //Las cantidad total de ContactMediums asociados al Owner es de 2
                assertTrue(ownerRepository.findById(ownerEntity.getId()).get().getContactMediums().size() == 2);
            }
        });

    }

    /*El siguiente test era necesario inicialmente cuando consideramos la restricción de que un Owner no podía tener una lista de ContactMediums vacía. Queda acá
    porque es relevante tener en cuenta la situación que se dió con respecto a la validación.
    @Test
    @DisplayName("Delete ContactMedium - Existing Owner related to single Pet and single ContactMedium - Should fail")
    @Transactional
    void deleteContactMedium_existingOwnerRelatedToSinglePetAndSingleContactMedium_shouldFail() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);

        //Creation of Pet
        Pet petEntityToDelete = new Pet();
        petEntityToDelete.setName("Fluffy");
        petEntityToDelete.setDescription("Good boy");
        ownerEntity.addPet(petEntityToDelete);

        //Creation of ContactMedium
        ContactMedium contactMediumEntityToDelete = new ContactMedium();
        contactMediumEntityToDelete.setType("Facebook");
        contactMediumEntityToDelete.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntityToDelete);

        //Persistence of Owner
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();

        //ACT
        Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(contactMediumEntityToDelete.getId());
        ContactMedium foundContactMedium = optionalContactMedium.get();
        Owner owner = foundContactMedium.getOwner();
        //Desasociación del ContactMedium
        owner.removeContactMedium(foundContactMedium);

        ownerRepository.flush();    //EN ESTE PUNTO ES QUE DEBERIA TRIGGEREARSE LA VALIDACIÓN

        //ASSERT
        //Validación explícita del Owner que no debería estar acá por el mismo motivo aclarado en PetRepositoryTests
        assertThrows(ConstraintViolationException.class, () -> {
                    Validator validator = validatorFactory.getValidator();
                    Set<ConstraintViolation<Owner>> violationSet = validator.validate(owner);
                    if (!violationSet.isEmpty()) {
                        throw new ConstraintViolationException(violationSet);
                    }
                    ownerRepository.flush();
                }
        );
    }*/
}
