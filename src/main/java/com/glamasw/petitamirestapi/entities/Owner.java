package com.glamasw.petitamirestapi.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotBlank(message = "El nombre del dueño de la mascota no puede estar vacío")
    @Column(name = "name")
    private String name;
    @Min(value = 1, message = "El DNI debe ser un valor positivo")
    @Column(name = "dni", unique = true)
    private int dni;
    @NotEmpty(message = "La lista de mascotas no puede estar vacía") //La anotación NotEmpty incluye también la validación de que la colección no tenga asociado un valor null.
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();
    @NotEmpty(message = "La lista de medios de contacto no puede estar vacía")
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ContactMedium> contactMediums = new ArrayList<>();

    //Constructor
    public Owner() {

    }

    //Getters and setters
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

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public List<ContactMedium> getContactMediums() {
        return contactMediums;
    }

    public void setContactMediums(List<ContactMedium> contactMediums) {
        this.contactMediums = contactMediums;
    }

    //Methods
    public void addPet(Pet pet) {
        this.pets.add(pet);
        pet.setOwner(this);
    }

    public void removePet(Pet pet) {
        this.pets.remove(pet);
        pet.setOwner(null);
    }

    public void addContactMedium(ContactMedium contactMedium) {
        this.contactMediums.add(contactMedium);
        contactMedium.setOwner(this);
    }

    public void removeContactMedium(ContactMedium contactMedium) {
       this.contactMediums.remove(contactMedium);
       contactMedium.setOwner(null);
    }

    @Override
    public boolean equals(Object obj) {
        //Check if the object is being compared to itself.
        if (this == obj) {
            return true;
        }
        //Check if the object is instance of this Owner.
        if(!(obj instanceof Owner)){
            return false;
        }
        //Typecast obj to Owner so that we can compare data members.
        Owner owner = (Owner)obj;

        return id == owner.getId()
                && name.equals(owner.getName())
                && dni == owner.getDni();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dni);
    }
}