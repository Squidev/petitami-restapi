package com.glamasw.petitamirestapi.IntegrationTests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Dog;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.DogRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest
@AutoConfigureMockMvc
public class DogRepositoryTests {

    @Autowired
    DogRepository dogRepository;

    /*You may not need unit testing for certain repository methods. Unit testing should not be done on trivial methods;
    if all you're doing is passing through a request for an object to ORM generated code and returning a result, you don't need to unit test that,
    in most cases; an integration test is adequate.*/

    @Test
    @DisplayName("Save Dog - Single Dog, single Owner, single ContactMedium - Should succeed")
    public void saveDog_singleDogSingleOwnerSingleContactMedium_shouldSucceed() {
        //ARRANGE PHASE
        //Population of DB
        populateDB();
        //Creation of Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("Good boy");
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.setContactMediums(new ArrayList<>());
        ownerEntity.getDogs().add(dogEntity);
        dogEntity.setOwner(ownerEntity);
        //Creation of ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        contactMediumEntity.setOwner(ownerEntity);
        ownerEntity.getContactMediums().add(contactMediumEntity);
        //ACT PHASE
        dogRepository.save(dogEntity);
        assertEquals(dogEntity, dogRepository.findById(dogEntity.getId()).get());
/*        } catch (Exception e) {
            e.toString();
        }
        assertSame(dogEntity.getDogOwner().getContactMediums().size(), 3);
        assertTrue(dogEntity.getDogOwner().getContactMediums().containsAll(contactMediumList));*/
    }

    /*
    @Test
    @DisplayName("Save a Dog: Contact mediums provided, each one including his own id, but not existing in DB yet")
    public void saveContactMediumsProvidingIdButNotStoredOnDBYet() throws Exception {
        //Entities init
        Dog dogEntity = new Dog();
        Owner ownerEntity = new Owner();
        List<ContactMedium> contactMediumList = new ArrayList<>();
        //Atributes setting
        dogEntity.setName("Chocoperro 2");
        ownerEntity.setName("Cochoperro Owner 2");
        ownerEntity.setDni(48419878);
        ownerEntity.setDogs(new ArrayList<>());
        //Contact mediums adding
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setName("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/ChocoperroOwner2");
        contactMediumEntity1.setOwner(ownerEntity);
        contactMediumList.add(contactMediumEntity1);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setName("Instagram");
        contactMediumEntity2.setValue("www.instagram.com/ChocoperroOwner2");
        contactMediumEntity2.setOwner(ownerEntity);
        contactMediumList.add(contactMediumEntity2);

        ContactMedium contactMediumEntity3 = new ContactMedium();
        contactMediumEntity3.setName("Twitter");
        contactMediumEntity3.setValue("www.twitter.com/ChocoperroOwner2");
        contactMediumEntity3.setOwner(ownerEntity);
        contactMediumList.add(contactMediumEntity3);

        ownerEntity.setContactMediums(contactMediumList);
        //Bidireccional Dog-Owner relationship
        dogEntity.setDogOwner(ownerEntity);
        ownerEntity.getDogs().add(dogEntity);

        try {
            dogRepository.save(dogEntity);
            //Testeamos si se puede modificar un ContactMedium no recuperandolo de la DB primero, sino creando uno nuevo y asignandoselo al Owner con el mismo id que tenia el de la DB
            //Entities init
            List<ContactMedium> contactMediumListReplacement = new ArrayList<>();
            //Atributes setting
            dogEntity.setName("Chocoperro 2");
            ownerEntity.setName("Cochoperro Owner 2");
            ownerEntity.setDni(48419878);
            ownerEntity.setDogs(new ArrayList<>());
            //Contact mediums adding
            ContactMedium contactMediumEntity1Replacement = new ContactMedium();
            contactMediumEntity1Replacement.setName("Facebook");
            contactMediumEntity1Replacement.setValue("www.facebook.com/OwnerReplacement");
            contactMediumEntity1Replacement.setOwner(ownerEntity);
            contactMediumListReplacement.add(contactMediumEntity1Replacement);

            ContactMedium contactMediumEntity2Replacement = new ContactMedium();
            contactMediumEntity2Replacement.setName("Instagram");
            contactMediumEntity2Replacement.setValue("www.instagram.com/OwnerReplacement");
            contactMediumEntity2Replacement.setOwner(ownerEntity);
            contactMediumListReplacement.add(contactMediumEntity2Replacement);

            ContactMedium contactMediumEntity3Replacement = new ContactMedium();
            contactMediumEntity3Replacement.setName("Twitter");
            contactMediumEntity3Replacement.setValue("www.twitter.com/OwnerReplacement");
            contactMediumEntity3Replacement.setOwner(ownerEntity);
            contactMediumListReplacement.add(contactMediumEntity3Replacement);

            ownerEntity.setContactMediums(contactMediumListReplacement);
            dogRepository.save(dogEntity);


        } catch (Exception e) {
            throw new Exception();
        }
        assertSame(dogEntity.getDogOwner().getContactMediums().size(), 3);
        assertTrue(dogEntity.getDogOwner().getContactMediums().containsAll(contactMediumList));
    }
*/
    void populateDB() {
        for (int i = 1; i <= 5; i++) {
            //Creation of Dog
            Dog dogEntity = new Dog();
            dogEntity.setName("Chocoperro " + i);
            dogEntity.setDescription("Description "+i);
            //Creation of Owner
            Owner ownerEntity = new Owner();
            ownerEntity.setDni(54149870+i);
            ownerEntity.setName("Chocoperro Owner " + i);
            ownerEntity.setDogs(new ArrayList<>());
            ownerEntity.setContactMediums(new ArrayList<>());
            ownerEntity.getDogs().add(dogEntity);
            dogEntity.setOwner(ownerEntity);
            //Creation of 3 ContactMedium
            ContactMedium contactMedium1 = new ContactMedium();
            contactMedium1.setType("Facebook");
            contactMedium1.setValue("www.facebook.com/ChocoperroOwner" + i);
            contactMedium1.setOwner(ownerEntity);
            ownerEntity.getContactMediums().add(contactMedium1);

            ContactMedium contactMedium2 = new ContactMedium();
            contactMedium2.setType("Instagram");
            contactMedium2.setValue("www.instagram.com/ChocoperroOwner" + i);
            contactMedium2.setOwner(ownerEntity);
            ownerEntity.getContactMediums().add(contactMedium2);

            ContactMedium contactMedium3 = new ContactMedium();
            contactMedium3.setType("Telefono");
            contactMedium3.setValue("261487510"+i);
            contactMedium3.setOwner(ownerEntity);
            ownerEntity.getContactMediums().add(contactMedium3);

            try {
                dogRepository.save(dogEntity);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}