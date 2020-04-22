package com.glamasw.petitamirestapi.IntegrationTests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Dog;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.DogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataIntegrityTests {

    @Autowired
    DogRepository dogRepository;

    /*-------------------------------------------
    AAA Testing

    The AAA (Arrange, Act, Assert) pattern is a common way of writing unit tests for a method under test.
    » Arrange section: initializes objects and sets the value of the data that is passed to the method under test.
    » Act section: invokes the method under test with the arranged parameters.
    » The Assert section verifies that the action of the method under test behaves as expected.
    ---------------------------------------------*/

    //La mayoría de todos estos test se basan en probar la validez de los datos ingresados. Para definir dicha validez
    //utilizamos las anotaciones provistas por el sistema Bean Validation que ofrece Spring. Dichas anotaciones nos
    //proveen de validaciones triviales que es innecesario testear. Lo hacemos en este proyecto a modo de exorcizarnos
    //la noobiness.

    @Test
    @DisplayName("Save Dog - Blank name - Should fail")
    public void saveDog_blankName_shouldFail(final TestInfo testInfo) {
        //ARRANGE PHASE
        //Creation of invalid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("");
        dogEntity.setDescription("Good boy");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.getDogs().add(dogEntity);
        dogEntity.setOwner(ownerEntity);
        ownerEntity.setContactMediums(new ArrayList<>());
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        contactMedium.setOwner(ownerEntity);
        ownerEntity.getContactMediums().add(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> dogRepository.save(dogEntity));   //Exception arrojada debido a la violación de la restricción definida por @NotBlank
        //If no exception is thrown, or if an exception of a different type is thrown, this method will fail.
        //The method assertThrows returns the thrown exception. Then below you can do whatever sort of assertions on the exception object you want.
        System.out.println(e.toString());
        //If you do not want to perform additional checks on the exception instance, simply ignore the return value.
    }

    @Test
    @DisplayName("Save Dog - Null description - Should fail")
    public void saveDog_nullDescription_shouldFail(final TestInfo testInfo) {
        //ARRANGE PHASE
        //Creation of invalid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.getDogs().add(dogEntity);
        dogEntity.setOwner(ownerEntity);
        ownerEntity.setContactMediums(new ArrayList<>());
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        contactMedium.setOwner(ownerEntity);
        ownerEntity.getContactMediums().add(contactMedium);
        //ACT AND ASSERT PHASE
        //A modo de ejemplo, la siguiente es una forma también válida pero obsoleta de implementar el testing de excepciones.
        try {
            dogRepository.save(dogEntity);
            fail();
        } catch (ConstraintViolationException e) { //Exception arrojada debido a la violación de la restricción definida por @NotNull
            assertTrue(true);
            System.out.println(e.getMessage());
        }
    }

    /*
    @Test
    @DisplayName("Save Dog - Photo constraint - Should fail")
    */

    @Test
    @DisplayName("Save Dog - Related to null Owner - Should fail")
    public void saveDog_relatedToNullOwner_shouldFail(final TestInfo testInfo) {
        //ARRANGE PHASE
        //Creation of invalid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("");
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> dogRepository.save(dogEntity));
        /* --------------------------------------------------------------------------------------------------------------------------
        When a lambda expression uses an assigned local variable from its enclosing space there is an important restriction.
        A lambda expression may only use local variable whose value doesn't change.
        That restriction is referred as "variable capture" which is described as; lambda expression capture values, not variables.
        Local variables referenced from an inner lambda expression must be final or effectively final.
        A variable which is not declared as final but whose value is never changed after it is first assigned is effectively final.
        ----------------------------------------------------------------------------------------------------------------------------*/
        System.out.println(e.toString());
    }
    /*
    @Test
    @DisplayName("Save Dog - Related to Owner but not related back - Should fail")
    public void saveDog_relatedToOwnerButNotRelatedBack_shouldFail() {
    }

    Este test no debería implementarse, dado que, en primer lugar, este caso nunca debería darse. Si se presenta, estamos
    ante un caso de fallo by design. Ambas responsabilidades, la de asignar un Owner a un Dog y el Dog a su Owner, deberían
    ser implementadas por el mismo método.
    */

    @Test
    @DisplayName("Save Dog - Blank Owner name - Should fail")
    public void saveDog_blankOwnerName_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("");
        //Creation of invalid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(23456789);
        ownerEntity.setName("");
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.setContactMediums(new ArrayList<>());
        ownerEntity.getDogs().add(dogEntity);
        dogEntity.setOwner(ownerEntity);
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        contactMedium.setOwner(ownerEntity);
        ownerEntity.getContactMediums().add(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> dogRepository.save(dogEntity));
        System.out.println(e.toString());
    }
    
//    @Test
//    @DisplayName("Save Dog - invalid DNI - Should fail")
//    public void saveDog_invalidDNI_shouldFail() {
//    }

    @Test
    @DisplayName("Save Dog - Null ContactMedium list - Should fail")
    public void saveDog_nullContactMediumList_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("");
        //Creation of invalid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        dogEntity.setOwner(ownerEntity);
        ownerEntity.getDogs().add(dogEntity);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> dogRepository.save(dogEntity));
        System.out.println(e.toString());
    }

    @Test
    @DisplayName("Save Dog - Empty ContactMedium list - Should fail")
    public void saveDog_emptyContactMediumList_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("");
        //Creation of invalid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.setContactMediums(new ArrayList<>());
        dogEntity.setOwner(ownerEntity);
        ownerEntity.getDogs().add(dogEntity);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> dogRepository.save(dogEntity));
        System.out.println(e.toString());
    }

    @Test
    @DisplayName("Save Dog - Blank ContactMedium type - Should fail")
    public void saveDog_blankContactMediumType_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.setContactMediums(new ArrayList<>());
        dogEntity.setOwner(ownerEntity);
        ownerEntity.getDogs().add(dogEntity);
        //Creation of invalid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("");
        contactMedium.setValue("www.facebook.com/John");
        contactMedium.setOwner(ownerEntity);
        ownerEntity.getContactMediums().add(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> dogRepository.save(dogEntity));
        System.out.println(e.toString());
    }

    @Test
    @DisplayName("Save Dog - Blank ContactMedium value - Should fail")
    public void saveDog_blankContactMediumValue_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.setContactMediums(new ArrayList<>());
        dogEntity.setOwner(ownerEntity);
        ownerEntity.getDogs().add(dogEntity);
        //Creation of invalid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("");
        contactMedium.setOwner(ownerEntity);
        ownerEntity.getContactMediums().add(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> dogRepository.save(dogEntity));
        System.out.println(e.toString());
    }

    @Test
    @DisplayName("Save Dog - Valid data - Should succeed")
    public void saveDog_validData_shouldSucceed(final TestInfo testInfo) {
        //ARRANGE PHASE
        //Creation of valid Dog
        Dog dogEntity = new Dog();
        dogEntity.setName("Fluffy");
        dogEntity.setDescription("Good boy");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.setDogs(new ArrayList<>());
        ownerEntity.getDogs().add(dogEntity);
        dogEntity.setOwner(ownerEntity);
        ownerEntity.setContactMediums(new ArrayList<>());
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        contactMedium.setOwner(ownerEntity);
        ownerEntity.getContactMediums().add(contactMedium);
        //ACT AND ASSERT PHASE
        dogRepository.save(dogEntity);
        assertDoesNotThrow(() -> dogRepository.save(dogEntity));
    }
}
