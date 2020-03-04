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
    @Column(name = "nombre")
    private String name;
    @Column(name = "dni", unique = true)
    private int dni;
    @OneToMany(mappedBy = "owner")
    private List<Dog> dogs;
    @OneToMany(mappedBy = "owner")
    private List<ContactMedium> contacts;
    

    public Owner(){


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

    public void setDogs(ArrayList<Dog> dogs) {
        this.dogs = dogs;
    }

    public List<ContactMedium> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<ContactMedium> contacts) {
        this.contacts = contacts;
    }

    
    
}