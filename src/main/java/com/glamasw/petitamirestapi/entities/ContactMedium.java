package com.glamasw.petitamirestapi.entities;

import javax.persistence.*;

@Entity
@Table(name = "contact_medium")
public class ContactMedium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "value")
    private String value;
    @ManyToOne
    @JoinColumn(name = "fk_owner", nullable = false)
    private Owner owner;

    public ContactMedium() {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}