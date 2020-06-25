package com.glamasw.petitamirestapi.IntegrationTests;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataIntegrityTests {

    @Autowired
    OwnerRepository ownerRepository;

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
    @DisplayName("Save Pet - Blank name - Should fail")
    @Transactional
    public void savePet_blankName_shouldFail(final TestInfo testInfo) {
        //ARRANGE PHASE
        //Creation of invalid Pet
        Pet petEntity = new Pet();
        petEntity.setName("");
        petEntity.setDescription("Good boy");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.addPet(petEntity);
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        ownerEntity.addContactMedium(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> ownerRepository.save(ownerEntity));   //Exception arrojada debido a la violación de la restricción definida por @NotBlank
        //If no exception is thrown, or if an exception of a different type is thrown, this method will fail.
        //The method assertThrows returns the thrown exception. Then below you can do whatever sort of assertions on the exception object you want.
        System.out.println(e.toString());
        //If you do not want to perform additional checks on the exception instance, simply ignore the return value.
    }

    @Test
    @DisplayName("Save Pet - Null description - Should fail")
    @Transactional
    public void savePet_nullDescription_shouldFail(final TestInfo testInfo) {
        //ARRANGE PHASE
        //Creation of invalid Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.addPet(petEntity);
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        ownerEntity.addContactMedium(contactMedium);
        //ACT AND ASSERT PHASE
        //A modo de ejemplo, la siguiente es una forma también válida pero obsoleta de implementar el testing de excepciones.
        try {
            ownerRepository.save(ownerEntity);
            fail();
        } catch (ConstraintViolationException e) { //Exception arrojada debido a la violación de la restricción definida por @NotNull
            assertTrue(true);
            System.out.println(e.getMessage());
        }
    }

    /*
    @Test
    @DisplayName("Save Pet - Photo constraint - Should fail")
    */

    /*
    @Test
    @DisplayName("Save Pet - Related to null Owner - Should fail")
    public void savePet_relatedToNullOwner_shouldFail(final TestInfo testInfo) {
    }

    @Test
    @DisplayName("Save Pet - Related to Owner but not related back - Should fail")
    public void savePet_relatedToOwnerButNotRelatedBack_shouldFail() {
    }

    Estos tests no deberían implementarse, dado que, en primer lugar, estos casos nunca deberían darse. Si se presentan, estamos
    ante un caso de fallo by design. Ambas responsabilidades, la de asignar un Owner a un Pet y el Pet a su Owner, deberían
    ser implementadas por el mismo método.
    */

    @Test
    @DisplayName("Save Pet - Blank Owner name - Should fail")
    @Transactional
    public void savePet_blankOwnerName_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("");
        //Creation of invalid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(23456789);
        ownerEntity.setName("");
        ownerEntity.addPet(petEntity);
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        ownerEntity.addContactMedium(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> ownerRepository.save(ownerEntity));
        /* --------------------------------------------------------------------------------------------------------------------------
        When a lambda expression uses an assigned local variable from its enclosing space there is an important restriction.
        A lambda expression may only use local variable whose value doesn't change.
        That restriction is referred as "variable capture" which is described as; lambda expression capture values, not variables.
        Local variables referenced from an inner lambda expression must be final or effectively final.
        A variable which is not declared as final but whose value is never changed after it is first assigned is effectively final.
        ----------------------------------------------------------------------------------------------------------------------------*/
        System.out.println(e.toString());
    }
    
//    @Test
//    @DisplayName("Save Pet - invalid DNI - Should fail")
//    public void savePet_invalidDNI_shouldFail() {
//    }

    //La siguiente situación no debería darse nunca, ya que por defecto inicializamos la variable contactMediums con un ArrayList vacío,
    //pero dejamos el test for the sake of ilustratividad.
    @Test
    @DisplayName("Save Pet - Null ContactMedium list - Should fail")
    @Transactional
    public void savePet_nullContactMediumList_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("");
        //Creation of invalid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.addPet(petEntity);
        //Forced null ContactMedium
        ownerEntity.setContactMediums(null);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> ownerRepository.save(ownerEntity));
        System.out.println(e.toString());
    }
/*
    //La restrición definida por la anotación @NotEmpty, además de validar que la lista no esté vacía, inicialmente valida que tampoco sea null
    //(no puede estar vacía si no existe, duh). Por lo que el test anterior es doblemente innecesario :^)
    @Test
    @DisplayName("Save Pet - Empty ContactMedium list - Should fail")
    @Transactional
    public void savePet_emptyContactMediumList_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("");
        //Creation of invalid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.addPet(petEntity);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> ownerRepository.save(ownerEntity));
        System.out.println(e.toString());
    }*/

    @Test
    @DisplayName("Save Pet - Blank ContactMedium type - Should fail")
    @Transactional
    public void savePet_blankContactMediumType_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.addPet(petEntity);
        //Creation of invalid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("");
        contactMedium.setValue("www.facebook.com/John");
        ownerEntity.addContactMedium(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> ownerRepository.save(ownerEntity));
        System.out.println(e.toString());
    }

    @Test
    @DisplayName("Save Pet - Blank ContactMedium value - Should fail")
    @Transactional
    public void savePet_blankContactMediumValue_shouldFail() {
        //ARRANGE PHASE
        //Creation of valid Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.addPet(petEntity);
        //Creation of invalid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("");
        ownerEntity.addContactMedium(contactMedium);
        //ACT AND ASSERT PHASE
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> ownerRepository.save(ownerEntity));
        System.out.println(e.toString());
    }

    @Test
    @DisplayName("Save Pet - Valid data - Should succeed")
    @Transactional
    public void savePet_validData_shouldSucceed(final TestInfo testInfo) {
        //ARRANGE PHASE
        //Creation of valid Pet
        Pet petEntity = new Pet();
        petEntity.setName("Fluffy");
        petEntity.setDescription("Good boy");
        //Creation of valid Owner
        Owner ownerEntity = new Owner();
        ownerEntity.setName("John");
        ownerEntity.setDni(23456789);
        ownerEntity.addPet(petEntity);
        //Creation of valid ContactMedium
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setType("Facebook");
        contactMedium.setValue("www.facebook.com/John");
        ownerEntity.addContactMedium(contactMedium);
        //ACT AND ASSERT PHASE
        ownerRepository.save(ownerEntity);
        assertDoesNotThrow(() -> ownerRepository.save(ownerEntity));
    }
}
