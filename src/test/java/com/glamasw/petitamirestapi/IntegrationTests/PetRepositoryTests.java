package com.glamasw.petitamirestapi.IntegrationTests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import com.glamasw.petitamirestapi.repositories.PetRepository;
import org.apache.el.util.Validation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.AssertTrue;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest
@AutoConfigureMockMvc
public class PetRepositoryTests {

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    PetRepository petRepository;
    static List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();
    @Autowired
    EntityManagerFactory entityManagerFactory;
    @Autowired
    ValidatorFactory validatorFactory;

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
    @DisplayName("Find all Pets - Should succeed")
    @Transactional
    void findAllPets_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //Array of all persisted pets
        ArrayList persistedPets = new ArrayList();
        for (Owner owner : ownerEntitiesForDBPopulation) {
            persistedPets.addAll(owner.getPets());
        }

        //ACT
        List<Pet> foundPets = petRepository.findAll();
        //ASSERT
        assertEquals(foundPets.size(), persistedPets.size());
        assertArrayEquals(persistedPets.toArray(), foundPets.toArray());
    }

    @Test
    @DisplayName("Save Pet - Existing Owner related to single Pet and single ContactMedium - Should succeed")
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
        //Creation of Pet to save
        Pet petToSave = new Pet();
        petToSave.setName("Biggie");
        petToSave.setDescription("Bad boy");

        //ACT
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
        Owner foundOwner = optionalOwner.get();
        petToSave.setOwner(foundOwner);
        petRepository.save(petToSave);

        //ASSERT
        assertTrue(petToSave.getId() != 0);
        assertTrue(petToSave.getOwner().getId() == ownerEntity.getId());
        assertTrue(foundOwner.getPets().size() == 2);
        assertEquals(foundOwner.getPets().get(1), petToSave);
    }

    @Test
    @DisplayName("Find Pet - By id - Should succeed")
    @Transactional
    void findPet_byId_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //Creation of Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(48419877);
        ownerEntity.setName("Fluffy Owner");
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
        Optional<Pet> optionalPet = petRepository.findById(petEntity.getId());
        Pet foundPet = optionalPet.get();
        //ASSERT
        //Pet data coincide
        assertEquals(foundPet, petEntity);
        //Owner data coincide
        assertEquals(foundPet.getOwner(), ownerEntity);
        //ContactMedium data coincide
        assertArrayEquals(foundPet.getOwner().getContactMediums().toArray(), ownerEntity.getContactMediums().toArray());
    }

    @Test
    @DisplayName("Find Pet - By UUID - Should succeed")
    @Transactional
    void findPet_byUUID_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //Creation of Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(48419877);
        ownerEntity.setName("Fluffy Owner");
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
        Optional<Pet> optionalPet = petRepository.findByUuid(petEntity.getUuid());
        Pet foundPet = optionalPet.get();
        //ASSERT
        //Pet data coincide
        assertEquals(foundPet, petEntity);
        //Owner data coincide
        assertEquals(foundPet.getOwner(), ownerEntity);
        //ContactMedium data coincide
        assertArrayEquals(foundPet.getOwner().getContactMediums().toArray(), ownerEntity.getContactMediums().toArray());
    }

    /* WEIRD OUTCOME
    @Test
    @DisplayName("Delete Pet - Id: 1 - Should succeed")
    @Transactional
    void deletePet_id1_shouldSucceed() {
        //ARRANGE
        //Búsqueda de la Pet a deletear
        Optional optionalPet = petRepository.findById(1);
        Pet petEntity = (Pet)optionalPet.get();

        //ACT
        //Owner foundOwner = (Owner)ownerRepository.findById(1).get();
        //foundOwner.deletePet(petEntity);
        petRepository.deleteById(1);
        //petRepository.flush();
        Owner foundOwner = (Owner)ownerRepository.findById(1).get();
        for (Pet pet:foundOwner.getPets()) {
            System.out.println(pet.getId() + "\n" + pet.getName() + "\n" + pet.getDescription());
        }
        //-----------------------------------------------------------------
        // En este punto, se supone que la Pet con id=1 ha sido deleteada, pero cuando recuperamos el respectivo Owner vemos
        // que la mascota sigue presente en el array. Sin embargo, el siguiente assert es exitoso, indicando que no existe la Pet
        // en la DB. La explicación que se me ocurre es que el Owner se encuentra actualmente instanciado (ya que la función populateDB()
        // donde es persistido, por medio del ownerRepository, es parte de esta transacción/sesión), y cuando se deletea la Pet por medio
        // del petRepository la query es ejecutada en la DB, pero el Owner instanciado no es actualizado. ¿Why is that?
        // 1. Si descomentamos las 2 primeras líneas de la fase ACT, efectivamente eliminando a la Pet del array antes de delegar la
        // instrucción del deleteo al petRepository, el comportamiento es el esperado.
        // 2. Si, en cambio, descomentamos la línea del flush() con la intención de forzar la actualización del estado actual del sistema
        // hacia la DB, el assert falla, indicando que la Pet no es eliminada de la DB. La explicación que se me ocurre es que si bien
        // deleteamos la Pet efectivamente de la DB, el respectivo Owner que se encuentra instanciado todavía tiene la Pet asociada,
        // y al hacer el flush esto es lo que es transmitido hacia la DB.
        //
        // UPDATE: Efectivamente esa era la wea. https://stackoverflow.com/a/52525033
        //         "If the removed Bar is referenced by a Foo, the PERSIST operation is cascaded from Foo to Bar because the association is
        //          marked with cascade = CascadeType.ALL and the deletion is unscheduled. To verify that this is happening, we may enable
        //          trace log level for the org.hibernate package and search for entries such as un-scheduling entity deletion."
        //          https://www.baeldung.com/delete-with-hibernate
        // SOLUCIÓN: ESTAR ATENTOS a si el Owner se encuentra instanciado en la actual sesión y, si ese es el caso, desasociar la Pet
        //           del mismo además de deletearlo por medio del petRepository.
        //---------------------------------------------------------------------

        //ASSERT
        assertTrue(petRepository.findById(1).isEmpty());
    }
    */

    /* USEFUL INFO

    A fundamental feature of JPA/Hibernate: All the changes made to attached entities are automatically made
    persistent: Hibernate manages them, so it compares their current state with their initial state, and automatically makes all the changes persistent.

    This is extremely useful, because you don't have to track all the entities that have been modified in a complex business method modifying lots of entities. And
    it's also efficient because Hibernate won't execute unnecessary SQL: if an entity hasn't changed during the transaction, no SQL update query will be executed for
    this entity. And if you modify entities and then throw an exception rollbacking the transaction, Hibernate will skip the updates.
    So, typical JPA code would look like this:

    void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account from = em.find(Account.class, fromAccountId); // from is managed by JPA
        Account to = em.find(Account.class, ftoAccountId); // to is managed by JPA
        from.remove(amount);
        to.add(amount);

        // now the transaction ends, Hibernate sees that the state of from and to
        // has changed, and it saves the entities automatically before the commit
    }

    persist() is used to make a new entity persistent, i.e. to make it managed by Hibernate.
    merge() is used to take a detached entity (i.e. an entity which is not managed by Hibernate, but already has an ID and a state) and to copy its state to the
    attached entity having the same ID. */

    @Test
    @DisplayName("Delete Pet - Existing Owner related to three Pets and three ContactMediums - Should succeed")
    @Transactional
    void deletePet_existingOwnerRelatedToThreePetsAndThreeContactMediums_shouldSucceed() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Pets
        Pet petEntityToDelete = new Pet();
        petEntityToDelete.setName("Fluffy");
        petEntityToDelete.setDescription("Good boy");

        Pet petEntity2 = new Pet();
        petEntity2.setName("Biggie");
        petEntity2.setDescription("Bad boy");

        Pet petEntity3 = new Pet();
        petEntity3.setName("Squishy");
        petEntity3.setDescription("Rubenesque boy");
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy, Biggie and Squishy Owner");
        ownerEntity.setDni(48419877);
        ownerEntity.addPet(petEntityToDelete);
        ownerEntity.addPet(petEntity2);
        ownerEntity.addPet(petEntity3);
        //Creation of ContactMediums
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setType("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/FluffyAndBiggieAndSquishyOwner");
        ownerEntity.addContactMedium(contactMediumEntity1);

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
        Optional<Pet> optionalPet = petRepository.findByUuid(petEntityToDelete.getUuid());
        Pet foundPet = optionalPet.get();
        Owner owner = foundPet.getOwner();
        //Desasociación de la Pet
        owner.removePet(foundPet);
        //Deleteo de la Pet
        petRepository.delete(foundPet);
        //ownerRepository.flush();

        /*
        Existen 2 formas de eliminar una entidad hija en una relación bidireccional:
        1. Explícitamente: Desasociar el objeto child del objeto parent y deletear el primero directamente mediante su respectivo repositorio.
        2. Implícitamente: Desasociar el objeto child del objeto parent habiendo definido la propiedad "orphanRemoval=true" en la anotación @OneToMany. Al momento del
            flush, el objeto hijo será deleteado automáticamente. Es necesario que la desasociación se realice en ambos extremos de la relación, tanto seteando al
            parent como null en el child como removiendo al child de la colección de children en el parent.
        IMPORTANTE: para que cualquiera de los 2 métodos funcione es necesario que ninguna de las entidades se encuentre redundantemente instanciada en alguna otra
        parte de la sesión con información inconsistente, ya que al momento del flush si existe otra instancia de una child con el parent no seteado a null, por
        ejemplo, Hibernate detectará que para dicho child no se realizan cambios a lo largo de la sesión y el deleteo del mismo será unscheduleado.
        CONCLUSIÓN: esta información cambia totalmente la forma en la que veníamos haciendo el testing. Por ejemplo, usar un repositorio para guardar una entidad al
        inicio del test, y luego deletearla mediante el mismo repositorio para comprobar si el proceso se realizó de forma efectiva, ya que al tratarse de la misma
        sesión, Hibernate schedulearía la persistencia de la entidad y luego la unschedulearía al llegar a la parte del deleteo, sin interacción alguna con la DB a lo
        largo del proceso. La forma correcta de implementar el test sería primero persistir las entidades correspondientes al test por medio de una sesión y luego
        testear los métodos respectivos del test por medio de una sesión distinta.
         */

        System.out.println("Pet entity to delete: " + petEntityToDelete + "\nPet entity to delete id: " + petEntityToDelete.getId() + "\nPet entity to delete Owner: " + petEntityToDelete.getOwner());
        System.out.println("Found Pet: " + foundPet + "\nFound Pet id: " + foundPet.getId() + "\nFound Pet Owner: " + foundPet.getOwner());

        //ASSERT
        //El Optional devuelto no incluye un Pet existente
        assertTrue(petRepository.findById(petEntityToDelete.getId()).isEmpty());
        //o por UUID
        assertTrue(petRepository.findByUuid(petEntityToDelete.getUuid()).isEmpty());
        //Las cantidad total de Pets pertenecientes al Owner es de 2
        assertTrue(ownerRepository.findById(owner.getId()).get().getPets().size() == 2);
    }

    @Test
    @DisplayName("Delete Pet - Existing Owner related to single Pet and three ContactMediums - Should fail")
    @Transactional
    void deletePet_existingOwnerRelatedToSinglePetAndThreeContactMediums_shouldFail() {
        //ARRANGE
        //Population of DB
        //populateDB();
        //Creation of Pets
        Pet petEntityToDelete = new Pet();
        petEntityToDelete.setName("Fluffy");
        petEntityToDelete.setDescription("Good boy");

        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy Owner");
        ownerEntity.setDni(48419877);
        ownerEntity.addPet(petEntityToDelete);

        //Creation of ContactMediums
        ContactMedium contactMediumEntity1 = new ContactMedium();
        contactMediumEntity1.setType("Facebook");
        contactMediumEntity1.setValue("www.facebook.com/FluffyAndBiggieAndSquishyOwner");
        ownerEntity.addContactMedium(contactMediumEntity1);

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
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.close();

        //ACT
        Optional<Pet> optionalPet = petRepository.findByUuid(petEntityToDelete.getUuid());
        Pet foundPet = optionalPet.get();
        Owner owner = foundPet.getOwner();
        //Desasociación de la Pet
        owner.removePet(foundPet);

        ownerRepository.flush();    //EN ESTE PUNTO ES QUE DEBERIA TRIGGEREARSE LA VALIDACIÓN


        System.out.println("Pet entity to delete: " + petEntityToDelete + "\npPet entity to delete id: " + petEntityToDelete.getId() + "\nPet entity to delete Owner: " + petEntityToDelete.getOwner());
        System.out.println("Found Pet: " + foundPet + "\nFound Pet id: " + foundPet.getId() + "\nFound Pet Owner: " + foundPet.getOwner());

        //ASSERT
        //Validación explícita del Owner
        /*
        El lifecycle de una entidad incluye 3 etapas: Persist, Update, Remove.
        Con Hibernate, la validación de una entidad es triggereada automáticamente durante los eventos PrePersist y PreUpdate por defecto, pero no en el PreRemove. Esto
        tiene sentido, ya que sería pointless validar las propiedades de una entidad que va a ser eliminada. En nuestro caso SI necesitamos validar que el deleteo de
        una Pet no deje a un Owner sin Pets asociadas en el sistema, hence, la siguiente validación explícita. Supuestamente es posible habilitar que el evento
        PreUpdate triggeree la validación, pero potencialmente involucra el uso de validation groups que leí es conveniente evitar, y de cualquier manera no sé si la
        validación triggereada sería sólo sobre la child entity deleteada y no sobre la parent entity.
        Supuse que el hecho de no deletear explícitamente a la Pet y dejar que el flush se encargara de eso (orphanRemoval=true mediante) correspondía a un update de
        la entidad, y la validación se triggerearía. Pero NOPE.
         */
        assertThrows(ConstraintViolationException.class, () -> {
                    Validator validator = validatorFactory.getValidator();
                    Set<ConstraintViolation<Owner>> violationSet = validator.validate(owner);
                    if (!violationSet.isEmpty()) {
                        throw new ConstraintViolationException(violationSet);
                    }
                    //Deleteo de la Pet
                    //petRepository.delete(foundPet);
                    ownerRepository.flush();
                }
        );
        /*
        El bloque de código anterior incluído en el assertThrows en realidad no debería ser parte de este test. Este test de por sí debería fallar automáticamente al
        flushearse el deleteo de la Pet hacia la DB, pero como desconozco actualmente cómo triggerear eso, incluyo acá la validación que DEBE ESTAR PRESENTE EN EL MÉTODO
        DEL SERVICE.
         */
    }

}