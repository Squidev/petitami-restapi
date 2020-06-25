package com.glamasw.petitamirestapi.IntegrationTests.RepositoryTests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import com.glamasw.petitamirestapi.repositories.PetRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.ValidatorFactory;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest
@AutoConfigureMockMvc
@Execution(ExecutionMode.SAME_THREAD)
public class PetRepositoryTests {

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    PetRepository petRepository;
    private static List<Owner> ownerEntitiesForDBPopulation = new ArrayList<>();
    // Por cada DataSource definido existirá un EntityManagerFactory que se utilizará para crear EntityManagers que manejaran las sesiones on demand que utilizaremos
    // para comunicarnos con ese DataSource.
    @Autowired
    EntityManagerFactory entityManagerFactory;
    @Autowired
    ValidatorFactory validatorFactory;
    @Autowired
    TransactionTemplate transactionTemplate;

    /*ISSUE: Al siguiente método, junto con el miembro ownerEntitiesForDBPopulation, inicialmente los definimos como static, para que antes de siquiera comenzar a
     ejecutarse la clase fueran definidos (mediante @BeforeAll) para ser usados a lo largo de los tests. El tema es que luego de persistir los Owner en la
     ejecución del primer test, los ids quedan inicializados, y en la ejecución del segundo test no pueden ser vueltos a guardar porque estan "sucios" con ids
     distintos de 0.
     Otro approach sería limpiarlos para cada test que se ejecute, para lo cual usaremos @BeforeEach. La idea de usar @BeforeAll era simplemente didáctica, por lo
     que no nos afecta, funcionalmente hablando.
     La solución elegida fue anotar las funciones setOwnerEntitiesForDBPopulation() y populateDB() con @BeforeEach, y nos encontramos con otro punto clave: NO se deben
     anotar 2 funciones cuyo orden de ejecución sea relevante con @BeforeEach, ya que el mismo no depende de la ubicación relativa de las funciones dentro de la clase,
     sino que es random (no random per se, pero a los efectos prácticos no es precisamente predecible). La solución a esto podría ser incluir la funcionalidad de
     populateDB() directamente dentro de setOwnerEntitiesForDBPopulation().
     Ahora, nos encontramos con que quizás en algun test se requiera no poblar la DB, como en el caso de testear un getAll() estando la DB vacía.
     La solución final es, entonces, anotar con @BeforeEach a setOwnerEntitiesForDBPopulation() y llamar a populateDB() desde los tests que lo requieran. Nos
     encontramos ahora con que los tests ejecutados independientemente funcionan, pero al ser ejecutados en conjunto no. Aparentemente el Owner persistido mediante
     el EntityManager dentro del primer test anotado con @Transactional ejecutado no es rollbackeado cuando la ejecución del test finaliza, por lo que el siguiente
     test falla al encontrarlo en la DB, y el siguiente, y el siguiente. Se supone que @Transactional debería rollbackear by default al finalizar la ejecución del
     test, pero that's not happening. Según la documentación de Spring: "@Rollback is a test annotation that is used to indicate whether a test-managed transaction
     should be rolled back after the test method has completed", pero tampoco estaría funcionando.

     SOLUTION: Resulta que los cambios que son rollbackeados en la DB son lo que se encuentran pendientes de ser commiteados, es decir, que han sido flusheados pero
     todavía no se han efectuado per se. El momento en el que realizamos un commit(), es el point of no return.
        Para respetar el principio number 1 de que "los tests deben ser repetibles e independientes entre sí", el enfoque inicialmente era ejecutar la función
     populateDB() al inicio de cada test, esperando que los efectos de ésta y del test en sí sean rollbackeados al finalizar la ejecución del mismo, y se repita el
     proceso para el siguiente. Pero esto no es posible mediante un rollback.

     MECHANICS 101: Cuando utilizamos el persistence context, sea via JpaRepository, EntityManager, etc., internamente Hibernate hace uso de la
     clase Session. Esto crea una via temporal y descartable que utilizamos para comunicarnos con la DB, lo que los altos sabios del Tibet llaman una "transacción". A
     lo largo de la transacción se mantiene abierta una sesión con la DB, mediante la cual utilizamos el flush() para sincronizar el estado de la misma con el del
     contexto de persistencia. Esos cambios se encuentran en la DB en memoria, y son volcados a las tablas correspondientes finalmente con el commit(). Por ello, en
     los @Transactional tests será rollbackeado by default todo aquello que haya sido FLUSHEADO PERO NO COMMITEADO. Tener en cuenta que este comportamiento se da en
     una DB que implementa un mecanismo de almacenamiento de datos que soporta transacciones, como InnoDB y no MyISAM.
        El tema ahora es que nos encontramos con 2 situaciones:
        1. Usamos el @Transactional en el test para mantener una transacción abierta con la DB a lo largo del mismo y así poder hacer uso de entities con LAZY
        inicialization, pero al usar cualquier JpaRepository para operar con la DB los cambios no serán persistidos a lo largo del test y estaremos trabajando con
        valores flusheados pero no efectivamente commiteados. El momento del commit será al finalizar la transacción, ergo, el test, pero éste no se llevará a cabo a
        menos que anotemos el test con @Commit, dado que los tests @Transactional se rollbackean by default. Y aunque utilicemos @Commit, tampoco nos sirve que esto se
        efectúe al final del test.
        2. No usamos el @Transactional en el test y, pero recibimos una exception al intentar trabajar con entities con LAZY inicialization.
        IMHO la solución sería forzar el commit del JpaRepository, pero no ofrece ninguna función para commitear explícitamente. Por lo que estoy investigando, en
        algunos casos (dependiendo del tipo de aplicación) la mejor manera de usar transacciones en los Spring tests es no utilizar @Transactional porque
        puede dar lugar a falsos positivos, en los que todo parece funcionar joyita, cuando en realidad estamos trabajando con los datos flusheados en la transacción
        actualmente abierta y no con datos realmente persistidos. Para esos casos TransactionTemplate es perfecto para controlar los transaction boundaries cuando
        requerimos de ese control.

            For real integration tests (that should behave exactly as in production) the answer is: definitely do not use @Transactional around test. But drawback also
            exists: you have to setup a database into well known state before every such test.

     Existen 2 formas de manejar código que deba ejecutarse en un contexto transaccional:
     1. Declarativa: mediante el uso de anotaciones como @Transaccional. La infraestructura de Spring facilita el uso de transacciones, al costo de exponernos a
     ciertos riesgos en cuanto al comportamiento de las mismas.
     2. Programáticamente: "envolviendo" el código que necesitamos que sea transaccional mediante el uso de:
        » TransactionTemplate: using the TransactionTemplate absolutely couples you to Spring’s transaction infrastructure and APIs. Whether or not programmatic
        transaction management is suitable for your development needs is a decision that you will have to make yourself.
        » Directamente una implementación de PlatformTransactionManager.

     La rule of thumb parece ser utilizar la forma declarativa sólo en métodos de la lógica de negocio para facilitar la implementación de la comunicación con la DB y
     reducir el boilerplate code, y utilizar la forma programática en los tests para simular distintas interacciones según lo que vayamos necesitando.

     TO DO: Investigar cómo el EntityManager se relaciona con el mecanismo de las transacciones. Por el momento ya se me fué demasiado tiempo enroscado con este tema,
     y el uso de TransactionTemplate no presentó problemas asi que I'll call it a day.
     */
    @BeforeEach //Es necesario reinicializar las entities a persistir debido a que después de cada test terminarán con ids ya establecidos
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
    @DisplayName("Find all Pets - Empty DB - Should return empty list of Pets")
    void findAllPets_emptyDB_shouldReturnEmptyListOfPets() {
        //ARRANGE
        //ACT
        List<Pet> foundPets = petRepository.findAll();
        //ASSERT
        assertTrue(foundPets.isEmpty());
    }

    @Test
    @DisplayName("Find all Pets - Populated DB - Should return full list of Pets")
    void findAllPets_populatedDB_shouldReturnFullListOfPets() {
        //ARRANGE
        //Population of DB
        populateDB();
        //Array of all persisted pets
        ArrayList persistedPets = new ArrayList();
        for (Owner owner : ownerEntitiesForDBPopulation) {
            persistedPets.addAll(owner.getPets());
        }

        //ACT
        List<Pet> foundPets = petRepository.findAll();
        //ASSERT
        assertTrue(foundPets.size() == persistedPets.size());
        assertEquals(foundPets, persistedPets);
    }

    @Test
    @DisplayName("Find Pet - By id - Should succeed")
    void findPet_byId_shouldSucceed() {
        //ARRANGE
        //Population of DB
        populateDB();
        //Creation of Pet
        Pet petEntity = new Pet();
        petEntity.setName("Sony");
        petEntity.setDescription("Fast boy");
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(48419877);
        ownerEntity.setName("Sony Owner");
        ownerEntity.addPet(petEntity);
        //Creation of ContactMedium
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType("Facebook");
        contactMediumEntity.setValue("www.facebook.com/SonyOwner");
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
                Optional<Pet> optionalPet = petRepository.findById(petEntity.getId());
                Pet foundPet = optionalPet.get();
                //Pet data coincide
                assertEquals(foundPet, petEntity);
                //Owner data coincide
                assertEquals(foundPet.getOwner(), ownerEntity);
                //ContactMedium data coincide
                assertArrayEquals(foundPet.getOwner().getContactMediums().toArray(), ownerEntity.getContactMediums().toArray());
            }
        });
    }

    @Test
    @DisplayName("Find Pet - By UUID - Should succeed")
    void findPet_byUUID_shouldSucceed() {
        //ARRANGE
        //Population of DB
        populateDB();
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

        //ACT AND ASSERT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<Pet> optionalPet = petRepository.findByUuid(petEntity.getUuid());
                Pet foundPet = optionalPet.get();
                //Pet data coincide
                assertEquals(foundPet, petEntity);
                //Owner data coincide
                assertEquals(foundPet.getOwner(), ownerEntity);
                //ContactMedium data coincide
                assertArrayEquals(foundPet.getOwner().getContactMediums().toArray(), ownerEntity.getContactMediums().toArray());
            }
        });
    }

    // El siguiente test se efectúa mediante 3 transacciones diferentes para asegurarnos de que cada etapa es independiente del resto y estamos trabajando con datos
    // actually persistidos en la DB.
    @Test
    @DisplayName("Save Pet - Existing Owner related to single Pet and single ContactMedium - Should succeed")
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
        //Creation of Pet to save
        Pet petToSave = new Pet();
        petToSave.setName("Biggie");
        petToSave.setDescription("Bad boy");

        //ACT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
                Owner foundOwner = optionalOwner.get();
                petToSave.setOwner(foundOwner);
                petRepository.save(petToSave);
            }
        });

        //ASSERT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<Owner> optionalOwner = ownerRepository.findById(ownerEntity.getId());
                Owner foundOwner = optionalOwner.get();
                assertTrue(petToSave.getId() != 0);
                assertEquals(petToSave.getOwner(), ownerEntity);
                assertTrue(foundOwner.getPets().size() == 2);
                assertEquals(foundOwner.getPets().get(1), petToSave);
            }
        });
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
    void deletePet_existingOwnerRelatedToThreePetsAndThreeContactMediums_shouldSucceed() {
        //ARRANGE
        //Population of DB
        populateDB();
        //Creation of Pets
        Pet petEntityToDelete = new Pet();
        petEntityToDelete.setName("Fluffy");
        petEntityToDelete.setDescription("Good boy");

        Pet petEntity2 = new Pet();
        petEntity2.setName("Biggie");
        petEntity2.setDescription("Bad boy");

        Pet petEntity3 = new Pet();
        petEntity3.setName("Fatty");
        petEntity3.setDescription("Rubenesque boy");
        //Creation of Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("Fluffy, Biggie and Fatty Owner");
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
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<Pet> optionalPet = petRepository.findById(petEntityToDelete.getId());
                Pet foundPet = optionalPet.get();
                //Desasociación de la Pet
                foundPet.getOwner().removePet(foundPet);
                //Deleteo de la Pet
                petRepository.delete(foundPet);
                ownerRepository.flush();

                System.out.println("Pet entity to delete: " + petEntityToDelete + "\nPet entity to delete id: " + petEntityToDelete.getId() + "\nPet entity to delete Owner: " + petEntityToDelete.getOwner());
                System.out.println("Found Pet: " + foundPet + "\nFound Pet id: " + foundPet.getId() + "\nFound Pet Owner: " + foundPet.getOwner());
            }
        });

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

        //ASSERT
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                //El Optional devuelto no incluye un Pet existente
                assertTrue(petRepository.findById(petEntityToDelete.getId()).isEmpty());
                //o por UUID
                assertTrue(petRepository.findByUuid(petEntityToDelete.getUuid()).isEmpty());
                //Las cantidad total de Pets pertenecientes al Owner es de 2
                assertTrue(ownerRepository.findById(ownerEntity.getId()).get().getPets().size() == 2);
            }
        });
    }

    /*
    El siguiente test era necesario inicialmente cuando consideramos la restricción de que un Owner no podía tener una lista de Pets vacía. Queda acá porque es
    relevante tener en cuenta la situación que se dió con respecto a la validación.
    @Test
    @DisplayName("Delete Pet - Existing Owner related to single Pet and three ContactMediums - Should fail due to Owner not related to any Pet")
    void deletePet_existingOwnerRelatedToSinglePetAndThreeContactMediums_shouldFailDueToOwnerNotRelatedToAnyPet() {
        //ARRANGE
        //Population of DB
        populateDB();
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
*//**//*        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Optional<Pet> optionalPet = petRepository.findById(petEntityToDelete.getId());
                Pet foundPet = optionalPet.get();
                //Desasociación de la Pet
                foundPet.getOwner().removePet(foundPet);
                ownerRepository.flush();    //EN ESTE PUNTO ES QUE DEBERIA(?) TRIGGEREARSE LA VALIDACIÓN

                System.out.println("Pet entity to delete: " + petEntityToDelete + "\npPet entity to delete id: " + petEntityToDelete.getId() + "\nPet entity to delete Owner: " + petEntityToDelete.getOwner());
                System.out.println("Found Pet: " + foundPet + "\nFound Pet id: " + foundPet.getId() + "\nFound Pet Owner: " + foundPet.getOwner());
            }
        });*//**//*

        //ASSERT
        //Validación explícita del Owner
        *//**//*
        El lifecycle de una entidad incluye 3 etapas: Persist, Update, Remove.
        Con Hibernate, la validación de una entidad es triggereada automáticamente durante los eventos PrePersist y PreUpdate por defecto, pero no en el PreRemove. Esto
        tiene sentido, ya que sería pointless validar las propiedades de una entidad que va a ser eliminada. En nuestro caso SI necesitamos validar que el deleteo de
        una Pet no deje a un Owner sin Pets asociadas en el sistema, hence, la siguiente validación explícita. Supuestamente es posible habilitar que el evento
        PreUpdate triggeree la validación, pero potencialmente involucra el uso de validation groups que leí es conveniente evitar, y de cualquier manera no sé si la
        validación triggereada sería sólo sobre la child entity deleteada y no sobre la parent entity.
        Supuse que el hecho de no deletear explícitamente a la Pet y dejar que el flush se encargara de eso (orphanRemoval=true mediante) correspondía a un update de
        la entidad, y la validación se triggerearía. Pero NOPE.
         *//**//*
        assertThrows(ConstraintViolationException.class, () -> {
                    Validator validator = validatorFactory.getValidator();
                    Set<ConstraintViolation<Owner>> violationSet = validator.validate(ownerEntity);
                    if (!violationSet.isEmpty()) {
                        throw new ConstraintViolationException(violationSet);
                    } else {
                        //Deleteo de la Pet
                        petRepository.delete(petRepository.findById(petEntityToDelete.getId()).get());
                        ownerRepository.flush();
                    }
                }
        );
        *//**//*
        El bloque de código anterior incluído en el assertThrows en realidad no debería ser parte de este test. Este test de por sí debería fallar automáticamente al
        flushearse el deleteo de la Pet hacia la DB, pero como desconozco actualmente cómo triggerear eso, incluyo acá la validación que DEBE ESTAR PRESENTE EN EL MÉTODO
        DEL SERVICE.
         *//*
    }*/
}