package com.glamasw.petitamirestapi.entities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dueño_id")
    private int id;
    @Column(name = "dueño_nombre")
    private String name;
    @Column(name = "dueño_dni", unique = true)
    private long dni;
    @OneToMany(mappedBy = "dueño")
    private ArrayList<Dog> dogs;
    @OneToMany
    @Column(name = "dueño_contactos")
    private ArrayList<ContactMedium> contacts;
    

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

    public long getDni() {
        return dni;
    }

    public void setDni(long dni) {
        this.dni = dni;
    }

    public ArrayList<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(ArrayList<Dog> dogs) {
        this.dogs = dogs;
    }

    public ArrayList<ContactMedium> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<ContactMedium> contacts) {
        this.contacts = contacts;
    }

    
    
}