package com.glamasw.petitamirestapi.entities;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "dog")
public class Dog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "El nombre de la mascota no puede estar vacío")     //La anotación NotBlank incluye también la validación de que la String no tenga asociado un valor null.
    @Column(name = "name")
    private String name;
    @NotNull(message = "La descripción de la mascota debe apuntar a una String válida")
    @Column(name = "description")
    private String description;
    @Lob
    @Column(name = "photo")
    private byte[] photo;
        @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_owner", nullable = false)    //If null, throws DataIntegrityViolationException, with a nested ConstraintViolationException
    @NotNull(message = "El perro debe estar relacionado con algún dueño") //If null, throws ConstraintViolationException
    private Owner owner;
    /*If both "@NotNull" and "nullable" are used, @NotNull validation applies first

    The @NotNull annotation is defined in the Bean Validation specification. In this case, our system threws javax.validation.ConstraintViolationException.
    It's important to notice that Hibernate doesn't trigger the SQL insert statement. Consequently, invalid data isn't saved to the database.
    This is because the pre-persist entity lifecycle event triggers the bean validation just before sending the query to the database.
    Also, out of the box, Hibernate translates the bean validation annotations applied to the entities into the DDL schema metadata.

    The @Column annotation is defined as a part of the Java Persistence API specification.
    It's used mainly in the DDL schema metadata generation. This means that if we let Hibernate generate the database schema automatically,
    it applies the not null constraint to the particular database column.
    First of all, Hibernate generates the column with the not null constraint.
    Additionally, it is able to create the SQL insert query and pass it through. As a result, it's the underlying database that triggers the error.

    Conclusion:
    Even though both of them prevent us from storing null values in the database, they take different approaches.
    As a rule of thumb, we should prefer the @NotNull annotation over the @Column(nullable = false) annotation.
    This way, we make sure the validation takes place before Hibernate sends any insert or update SQL queries to the database.
    Also, it's usually better to rely on the standard rules defined in the Bean Validation, rather than letting the database handle the validation logic.
    But, even if we let Hibernate generate the database schema, it'll translate the @NotNull annotation into the database constraints.
    We must then only make sure that hibernate.validator.apply_to_ddl property is set to true.
    Source: https://www.baeldung.com/hibernate-notnull-vs-nullable
    */
    public Dog() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

/*  Función equals() (Analizando si actually it's worth implementing)

    //Overriding equals() to compare two Dog objects
    //La idea de implementar este método es que en los tests podamos utilizar assertEquals() para verificar si dos Dog son estructuralmente el mismo objeto.
    //assertEquals() utiliza la función equals() para hacer la comparación. En caso de no estar redefinido dicho método en la clase, por defecto simplemente se compara
    //si los valores de los punteros son iguales.
    @Override
    public boolean equals(Object obj) {

        //If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }

        //Check if obj is an instance of Dog or not. "null instanceof [type]" also returns false
        if (!(obj instanceof Dog)) {
            return false;
        }

        //Typecast obj to Dog so that we can compare data members
        Dog dog = (Dog) obj;

        //Compare the data members and return accordingly
        //Aquí nos encontramos con un conundrum(awante Sabrina y su vocabulario papá). Lo ideal es comparar miembro por miembro los dos objetos para determinar
        //su equivalencia tête-à-tête(wena, entraba a mandar fruta :v). La situación se complejiza cuando se trabaja con clases que presentan cierto grado de acoplamiento.
        //Por ejemplo, dado el caso de la relación bidireccional, chequearemos si los miembros de la clase Dog tienen los mismos valores para ambos objetos, y luego pasaremos
        //a comparar los valores de los miembros de los respectivos Owner asociados a cada Dog. Pero cuando lleguemos al miembro "dogs", deberemos comprobar que para cada Dog
        //perteneciente al array del primer objeto existe un Dog equivalente en el array del segundo objeto, y que el segundo no incluye otros Dogs además de los ya chequeados.
        //Anyway, it's doable, aunque jodido.

        //GOOD NEWS: Una forma de simplificar la implementación es la siguiente. Si existen claves de negocio que identifiquen unívocamente a un objeto, bastará con comparar si dos objetos
        //poseen la misma clave para verificar que se trata del mismo objeto, y por lo tanto, son estructuralmente iguales.

        //BAD NEWS: La mencionada implementación no nos sirve para ser utilizada en el assertEquals(), ya que la idea del test es comparar a un objeto mandado a persistir con
        //el mismo objeto recuperado con otro método del sistema de persistencia, y como ya sabemos, el objeto mandado a persistir tiene un id=0 y el que recibiremos tiene un
        //id!=0.

        //Una solución a esto es fijarnos si existen claves primarias naturales (DNI, nroFactura, productUUID, etc.) en nuestro sistema cuyo valor sea conocido antes de
        //persistir el objeto y que podamos aprovechar para realizar dicha comparación.

        if
        for (ContactMedium cm: this.getOwner().getContactMediums()) {

        }


        return id == dog.getId()
                && name.compareTo(dog.getName()) == 0
                && description.compareTo(dog.getDescription()) == 0
                && photo.equals(dog.getPhoto())
                && owner.getId() == dog.getOwner().getId()
                && owner.getName().compareTo(dog.getOwner().getName()) == 0
                && owner.getDni() == dog.getOwner().getDni()
                && owner.getContactMediums().

    }
}
*/
}
