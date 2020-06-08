package com.glamasw.petitamirestapi.IntegrationTests;

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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    static List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();

    @BeforeAll
    static void setOwnerEntitiesForDBPopulation() {
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
    void populateDB () {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        for (Owner owner: ownerEntitiesForDBPopulation) {
            entityManager.persist(owner);
        }
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();
    }

    @Test
    @DisplayName("Find all ContactMediums - Should succeed")
    @Transactional
    void findAllContactMediums_shouldSucceed(){
        //ARRANGE
        //Population of DB
        //Array of all persisted ContactMediums
        List<ContactMedium> persistedContactMediums = new ArrayList<>();
        for (Owner ownerEntity : ownerEntitiesForDBPopulation) {
            persistedContactMediums.addAll(ownerEntity.getContactMediums());
        }
        //ACT
        List<ContactMedium> foundContactMediums = contactMediumRepository.findAll();
        //ASSERT
        assertArrayEquals(foundContactMediums.toArray(), persistedContactMediums.toArray());
    }

    @Test
    @DisplayName("Save ContactMedium - Existing Owner related to single Pet and single ContactMedium - Should succeed")
    @Transactional
    void savePet_existingOwnerRelatedToSinglePetAndSingleContactMedium_shouldSucceed() {
        //ARRANGE
        //Population of DB
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
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
        Owner foundOwner = optionalOwner.get();
        contactMediumToSave.setOwner(foundOwner);
        contactMediumRepository.save(contactMediumToSave);

        //ASSERT
        assertTrue(contactMediumToSave.getId() != 0);
        assertTrue(contactMediumToSave.getOwner().getId() == ownerEntity.getId());
        assertTrue(foundOwner.getContactMediums().size() ==2);
        assertEquals(foundOwner.getContactMediums().get(1), contactMediumToSave);
    }

    @Test
    @DisplayName("Find ContactMedium - By id - Should succeed")
    @Transactional
    void findContactMedium_byId_shouldSucceed() {
        //ARRANGE
        //Population of DB
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
        //ACT
        Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(contactMediumEntity.getId());
        ContactMedium foundContactMedium = optionalContactMedium.get();
        //ASSERT
        //Pet data coincide
        assertEquals(foundContactMedium, contactMediumEntity);
        //Owner data coincide
        assertEquals(foundContactMedium.getOwner(), ownerEntity);
        //ContactMedium data coincide
        assertArrayEquals(foundContactMedium.getOwner().getPets().toArray(), ownerEntity.getPets().toArray());
    }

    @Test
    @DisplayName("Delete ContactMedium - Existing Owner related to three Pets and three ContactMediums - Should succeed")
    @Transactional
    void deleteContactMedium_existingOwnerRelatedToThreePetsAndThreeContactmediums_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy, Biggie and Squishy Owner");
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
        petEntity3.setName("Squishy");
        petEntity3.setDescription("Rubenesque boy");
        ownerEntity.addPet(petEntity3);

        //Creation of ContactMediums
        ContactMedium contactMediumEntityToDelete = new ContactMedium();
        contactMediumEntityToDelete.setType("Facebook");
        contactMediumEntityToDelete.setValue("www.facebook.com/FluffyAndBiggieAndSquishyOwner");
        ownerEntity.addContactMedium(contactMediumEntityToDelete);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setType("Instagram");
        contactMediumEntity2.setValue("www.instagram.com/FluffyAndBiggieAndSquishyOwner");
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
        Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(contactMediumEntityToDelete.getId());
        ContactMedium foundContactMedium = optionalContactMedium.get();
        Owner owner = foundContactMedium.getOwner();
        //Desasociación del ContactMedium
        owner.removeContactMedium(foundContactMedium);
        //Deleteo del ContactMedium
        contactMediumRepository.delete(foundContactMedium);

        //ASSERT
        //El Optional devuelto no incluye un ContactMedium existente
        assertTrue(contactMediumRepository.findById(contactMediumEntityToDelete.getId()).isEmpty());
        //Las cantidad total de ContactMediums asociados al Owner es de 2
        assertTrue(ownerRepository.findById(owner.getId()).get().getContactMediums().size() == 2);
    }

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
    }
}
