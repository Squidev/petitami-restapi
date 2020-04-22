package com.glamasw.petitamirestapi.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
    @Column(name = "dni", unique = true)
    private int dni;
    @NotEmpty(message = "La lista de Dog no puede estar vacía") //La anotación NotEmpty incluye también la validación de que la colección no tenga asociado un valor null.
    @OneToMany(mappedBy = "dogOwner", orphanRemoval = true)
    private List<Dog> dogs;
    @NotEmpty(message = "La lista de ContactMedium no puede estar vacía")
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ContactMedium> contactMediums;

    public Owner() {

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

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }

    public List<ContactMedium> getContactMediums() {
        return contactMediums;
    }

    public void setContactMediums(List<ContactMedium> contactMediums) {
        this.contactMediums = contactMediums;
    }

}