package com.glamasw.petitamirestapi.IntegrationTests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.repositories.ContactMediumRepository;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import com.glamasw.petitamirestapi.repositories.PetRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest
@AutoConfigureMockMvc
@Execution(ExecutionMode.CONCURRENT)

public class OwnerRepositoryTests {

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    ContactMediumRepository contactMediumRepository;
    @Autowired
    EntityManagerFactory entityManagerFactory;
    static List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();


    /*You may not need unit testing for certain repository methods. Unit testing should not be done on trivial methods;
    if all you're doing is passing through a request for an object to ORM generated code and returning a result, you don't need to unit test that,
    in most cases; an integration test is adequate.*/

    //Método que se ejecutará antes que los tests.
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

    @Test
    @DisplayName("Find all owners - Should succeed")
    @Transactional
    //This will cause call to findAllOwners_shouldSucceed() to run inside a transaction (participating in an existing one or creating a new one if none already running).
    //En este caso, si no utilizamos la anotación @Transactional, al intentar comparar Owners en la ejecución de assertArrayEquals(), este test arrojará la siguiente excepción:
    //org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.glamasw.petitamirestapi.entities.Owner.contactMediums, could not initialize proxy - no Session
    //Esto se debe a que deberá existir una sesión activa para que los Dog y ContactMedium asociados a un Owner puedan ser inicializados de forma perezosa,
    //al momento de hacer la comparación.
/*    You need to either use @ManyToMany(fetch = FetchType.EAGER) to automatically pull back child entities, but it is not recommendable
      A better option would be to implement a spring transactionManager by adding the following to your spring XML configuration file:
        <bean id="transactionManager"
            class="org.springframework.orm.hibernate4.HibernateTransactionManager">
            <property name="sessionFactory" ref="sessionFactory" />
        </bean>
        <tx:annotation-driven />

    You can then add an @Transactional annotation to your method like so:

    @Transactional
    public Authentication authenticate(Authentication authentication)

    This will then start a db transaction for the duration of the authenticate method allowing any lazy collection to be retrieved from the db as and when you try to use them.
*/
    public void findAllOwners_shouldSucceed() {
        //ARRANGE PHASE
        //Population of DB
        //ACT PHASE
        List<Owner> ownerEntitiesFound = ownerRepository.findAll();
        //ASSERT PHASE
        assertTrue(ownerEntitiesFound.size()==ownerEntitiesForDBPopulation.size());
        assertArrayEquals(ownerEntitiesFound.toArray(), ownerEntitiesForDBPopulation.toArray());
    }

    @Test
    @DisplayName("Save Owner - Single Pet, single Owner, single ContactMedium - Should succeed")
    @Transactional
    //Luego de ejecutarse el test, se rollbackeará su efecto. La anotación @BeforeEach en el método populateDB() prácticamente hace que
    //dicho bloque de código sea incluido inmediatamente al inicio, dentro del bloque de código de cada test. Por lo que la
    //anotación @Transactional también invertirá su efecto.
    public void saveOwner_singleDogSingleOwnerSingleContactMedium_shouldSucceed() {
        //ARRANGE PHASE
        //Population of DB
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
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

        //ACT PHASE
        ownerRepository.save(ownerEntity);

        //ASSERT PHASE
        assertTrue(ownerEntity.getId() != 0);
        assertTrue(petEntity.getId() != 0);
        assertTrue(contactMediumEntity.getId() != 0);
    }

    @Test
    @DisplayName("Save Owner - Single Pet, single Owner, three ContactMedium - Should succeed")
    @Transactional
    public void saveOwner_singlePetSingleOwnerThreeContactMedium_shouldSucceed() {
        //ARRANGE PHASE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
        //Creation of Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        ownerEntity.addPet(petEntity);
        //Creation of 3 ContactMedium
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setType("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity1);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setType("Instagram");
        contactMediumEntity2.setValue("www.instagram.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity2);

        ContactMedium contactMediumEntity3 = new ContactMedium();
        contactMediumEntity3.setType("Telefono");
        contactMediumEntity3.setValue("2618457815");
        ownerEntity.addContactMedium(contactMediumEntity3);

        //ACT PHASE
        ownerRepository.save(ownerEntity);

        //ASSERT PHASE
        assertTrue(ownerEntity.getId() != 0);
        assertTrue(petEntity.getId() != 0);
        assertTrue(contactMediumEntity1.getId() != 0);
        assertTrue(contactMediumEntity2.getId() != 0);
        assertTrue(contactMediumEntity3.getId() != 0);
    }

    @Test
    @DisplayName("Save Owner - Two Pet, single Owner, single ContactMedium - Should succeed")
    @Transactional
    //Luego de ejecutarse el test, se rollbackeará su efecto. La anotación @BeforeEach prácticamente hace que
    //dicho bloque de código sea incluido inmediatamente al inicio, dentro del bloque de código de cada test. Por lo que la
    //anotación @Transactional también invertirá su efecto.
    public void saveOwner_twoPetSingleOwnerSingleContactMedium_shouldSucceed() {
        //ARRANGE PHASE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
        //Creation of 2 Pet
        Pet petEntity1 = new Pet();
        petEntity1.setName("Fluffy");
        petEntity1.setDescription("Good boy");
        ownerEntity.addPet(petEntity1);

        Pet petEntity2 = new Pet();
        petEntity2.setName("Biggie");
        petEntity2.setDescription("Bad boy");
        ownerEntity.addPet(petEntity2);
        //Creation of ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/FluffyOwner");
        ownerEntity.addContactMedium(contactMediumEntity);

        //ACT PHASE
        ownerRepository.save(ownerEntity);

        //ASSERT PHASE
        assertTrue(petEntity1.getId() != 0);
        assertTrue(petEntity2.getId() != 0);
        assertTrue(ownerEntity.getId() != 0);
        assertTrue(contactMediumEntity.getId() !=0);
    }

    @Test
    @DisplayName("Save Owner - Two Pet, single Owner, three ContactMediums - Should succeed")
    @Transactional
    public void saveOwner_twoPetSingleOwnerThreeContactMedium_shouldSucceed() {
        //ARRANGE PHASE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
        //Creation of 2 Pet
        Pet petEntity1 = new Pet();
        petEntity1.setName("Fluffy");
        petEntity1.setDescription("Good boy");
        ownerEntity.addPet(petEntity1);

        Pet petEntity2 = new Pet();
        petEntity2.setName("Biggie");
        petEntity2.setDescription("Bad boy");
        ownerEntity.addPet(petEntity2);
        //Creation of ContactMedium
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setType("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/FluffyAndBiggieOwner");
        ownerEntity.addContactMedium(contactMediumEntity1);

        ContactMedium contactMediumEntity2 = new ContactMedium();
        contactMediumEntity2.setType("Instagram");
        contactMediumEntity2.setValue("www.instagram.com/FluffyAndBiggieOwner");
        ownerEntity.addContactMedium(contactMediumEntity2);

        ContactMedium contactMediumEntity3 = new ContactMedium();
        contactMediumEntity3.setType("Telefono");
        contactMediumEntity3.setValue("2614875579");
        ownerEntity.addContactMedium(contactMediumEntity3);

        //ACT PHASE
        ownerRepository.save(ownerEntity);

        //ASSERT PHASE
        assertTrue(petEntity1.getId() != 0);
        assertTrue(petEntity2.getId() != 0);
        assertTrue(ownerEntity.getId() != 0);
        assertTrue(contactMediumEntity1.getId() !=0);
        assertTrue(contactMediumEntity2.getId() !=0);
        assertTrue(contactMediumEntity3.getId() !=0);
    }

    @Test
    @DisplayName("Find Owner - By DNI - Should succeed")
    @Transactional
    void findOwner_byDni_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
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
        Optional<Owner> optionalOwner = ownerRepository.findByDni(48419877);
        Owner foundOwner = optionalOwner.get();

        //ASSERT
        assertEquals("Fluffy Owner", foundOwner.getName());
        assertEquals(1, foundOwner.getPets().size());
        assertEquals("Fluffy", foundOwner.getPets().get(0).getName());
        assertEquals("Good boy", foundOwner.getPets().get(0).getDescription());
        assertEquals(1, foundOwner.getContactMediums().size());
        assertEquals("Facebook", foundOwner.getContactMediums().get(0).getType());
        assertEquals("www.facebook.com/FluffyOwner", foundOwner.getContactMediums().get(0).getValue());

        //Con lo siguiente vemos que ambas variables apuntan al mismo objeto. Hibernate, al tratarse este bloque
        //de código de una sola sesión (definido por @Transactional), al recuperar el Owner de la DB detecta
        //que dicho Owner ya se encuentra instanciado en la sesión, y se encarga de mantener la consistencia
        //devolviendo el mismo Owner instanciado en lugar de duplicarlo.
        System.out.println(ownerEntity);
        System.out.println(foundOwner);
    }

    @Test
    @DisplayName("Find Owner - By id - Should succeed")
    @Transactional
    void findOwner_byId_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
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
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
        Owner foundOwner = optionalOwner.get();

        //ASSERT
        assertEquals("Fluffy Owner", foundOwner.getName());
        assertEquals(1, foundOwner.getPets().size());
        assertEquals("Fluffy", foundOwner.getPets().get(0).getName());
        assertEquals("Good boy", foundOwner.getPets().get(0).getDescription());
        assertEquals(1, foundOwner.getContactMediums().size());
        assertEquals("Facebook", foundOwner.getContactMediums().get(0).getType());
        assertEquals("www.facebook.com/FluffyOwner", foundOwner.getContactMediums().get(0).getValue());

        //Idem previous test
        System.out.println(ownerEntity);
        System.out.println(foundOwner);
    }

/*  Pending: Find out why the following test is failing
    @Test
    @DisplayName("Update Owner - Update Owner data - Should succeed")
    @Transactional
    void updateOwner_updateOwnerData_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
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
        System.out.println("---Persisting owner---");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ownerEntity);
        entityManager.getTransaction().commit();
        entityManager.clear();
        System.out.println("---Owner persisted---");

        //ACT
        System.out.println("---Finding persisted owner---");
        Optional<Owner> optionalOwner = ownerRepository.findById(1);
        Owner foundExistingOwner = optionalOwner.get();
        System.out.println("---Found persisted owner---");
        foundExistingOwner.setName("Updated Owner");
        foundExistingOwner.setDni(3345781);
        System.out.println("---Saving updated owner---");
        ownerRepository.flush();
        System.out.println("---Saved updated owner---");

        //ASSERT
        System.out.println("---Finding updated owner---");
        entityManager.getTransaction().begin();
        Owner foundUpdatedOwner = entityManager.find(Owner.class, ownerEntity.getId());
        entityManager.getTransaction().commit();
        entityManager.close();
        System.out.println("---Found updated owner---");
        assertEquals("Updated Owner", foundUpdatedOwner.getName());
        assertEquals(3345781, foundUpdatedOwner.getDni());
        //A modo de comprobación visual
        List<Owner> owners = ownerRepository.findAll();
        for (Owner o:owners) {
            System.out.println(o.getName());
        }
    }*/

    @Test
    @DisplayName("Update Owner - Update Owner data - Should succeed")
    @Transactional()
    void updateOwner_updateOwnerData_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
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
        System.out.println("---Persisting owner---");
        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        entityManager1.getTransaction().begin();
        entityManager1.persist(ownerEntity);
        entityManager1.getTransaction().commit();
        entityManager1.detach(ownerEntity);
        entityManager1.clear();
        entityManager1.close();
        System.out.println("---Owner persisted---");
        int id = ownerEntity.getId();

        //ACT
        System.out.println("---Finding persisted owner---");
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
        ownerEntity = null;
        petEntity =null;
        contactMediumEntity=null;
        Owner foundExistingOwner = optionalOwner.get();
        System.out.println("---Found persisted owner---");
        foundExistingOwner.setName("Updated Owner");
        foundExistingOwner.setDni(3345781);
        System.out.println("---Saving updated owner---");
        ownerRepository.flush();
        System.out.println("---Saved updated owner---");
        System.out.println("foundExistingOwner.name: " + foundExistingOwner.getName());
        foundExistingOwner = null;
        //ASSERT
        System.out.println("---Finding updated owner---");
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();
        entityManager2.getTransaction().begin();
        Owner foundUpdatedOwner = entityManager2.find(Owner.class, id);
        System.out.println("foundUpdatedOwner.name: " + foundUpdatedOwner.getName());
        entityManager2.getTransaction().commit();
        entityManager2.clear();
        entityManager2.close();
        System.out.println("---Found updated owner---");

        System.out.println("entityManager1: " + entityManager1);
        System.out.println("entityManager2: " + entityManager2);
        //A modo de comprobación visual
        List<Owner> owners = ownerRepository.findAll();
        for (Owner o : owners) {
            System.out.println(o.getName());
        }

        assertEquals("Updated Owner", foundUpdatedOwner.getName());
        assertEquals(3345781, foundUpdatedOwner.getDni());

    }

    @Test
    @DisplayName("Update Owner - Update Pet data - Should succeed")
    @Transactional
    void updateOwner_updatePetData_shouldSucceed() {
        //ARRANGE PHASE
        Optional<Owner> optionalOwner = ownerRepository.findById(1);
        Owner foundOwner = optionalOwner.get();
        foundOwner.getPets().get(0).setName("Updated Pet");
        foundOwner.getPets().get(0).setDescription("Updated description");
        //ACT PHASE
        ownerRepository.save(foundOwner);
        Optional<Owner> optionalOwner2 = ownerRepository.findById(1);
        Owner foundOwner2 = optionalOwner2.get();
        //ASSERT PHASE
        assertTrue(foundOwner2.getPets().get(0).getName().compareTo("Updated Pet") == 0);
        assertTrue(foundOwner2.getPets().get(0).getDescription().compareTo("Updated description") == 0);
        //A modo de comprobación visual
        List<Owner> owners = ownerRepository.findAll();
        for (Owner o: owners) {
            System.out.println("--------------------------");
            System.out.println(o.getName() + " pets:");
            for (Pet e: o.getPets()) {
                System.out.println("Pet name: " + e.getName());
                System.out.println("Pet description: " + e.getDescription());
            }
        }
    }

    @Test
    @DisplayName("Update Owner - Update ContactMedium data - Should succeed")
    @Transactional
    void updateOwner_updateContactMediumData_shouldSucceed() {
        //ARRANGE PHASE
        Optional<Owner> optionalOwner = ownerRepository.findById(1);
        Owner foundOwner = optionalOwner.get();
        foundOwner.getContactMediums().get(0).setType("Updated type");
        foundOwner.getContactMediums().get(0).setValue("Updated value");
        //ACT PHASE
        ownerRepository.save(foundOwner);
        Optional<Owner> optionalOwner2 = ownerRepository.findById(1);
        Owner foundOwner2 = optionalOwner2.get();
        //ASSERT PHASE
        assertTrue(foundOwner2.getContactMediums().get(0).getType().compareTo("Updated type") == 0);
        assertTrue(foundOwner2.getContactMediums().get(0).getValue().compareTo("Updated value") == 0);
        //A modo de comprobación visual
        List<Owner> owners = ownerRepository.findAll();
        for (Owner o : owners) {
            System.out.println("--------------------------");
            System.out.println(o.getName() + " contact mediums:");
            for (ContactMedium cm : o.getContactMediums()) {
                System.out.println("ContactMedium type: " + cm.getType());
                System.out.println("Contactmedium value: " + cm.getValue());
            }
        }
    }

    @Test
    @DisplayName("Delete Owner - Id: 1 - Should succeed")
    @Transactional
    void deleteOwner_id1_shouldSucceed() {
        //ARRANGE
        //Búsqueda del Owner a deletear
        Optional optionalOwner = ownerRepository.findById(1);
        Owner ownerEntity = (Owner)optionalOwner.get();
        //Pets que NO deberían existir luego del deleteo
        ArrayList<Integer> idsOfPetsToDelete = new ArrayList<>();
        for (Pet pet: ownerEntity.getPets()) {
            idsOfPetsToDelete.add(pet.getId());
            System.out.println(pet);
        }
        //ContactMediums que NO deberían existir luego del deleteo
        ArrayList<Integer> idsOfContactMediumsToDelete = new ArrayList<>();
        for (ContactMedium cm: ownerEntity.getContactMediums()) {
            idsOfContactMediumsToDelete.add(cm.getId());
            System.out.println(cm);
        }

        //ACT
        ownerRepository.deleteById(1);

        //ASSERT
        //El Optional devuelto no incluye un Owner existente
        assertTrue(ownerRepository.findById(1).isEmpty());
        //Ninguna de las Pets inicialmente asociadas al Owner existen
        assertTrue(petRepository.findAllById(idsOfPetsToDelete).isEmpty());
        //Ninguno de los ContactMediums inicialmente asociados al Owner existen
        assertTrue(contactMediumRepository.findAllById(idsOfContactMediumsToDelete).isEmpty());
    }




}