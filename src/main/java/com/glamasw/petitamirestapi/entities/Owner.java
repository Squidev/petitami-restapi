package com.glamasw.petitamirestapi.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "dni", unique = true)
    private int dni;
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<Dog> dogs;
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