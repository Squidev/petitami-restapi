package com.glamasw.petitamirestapi.dtos;

/**
 * ContactMediumDTO
 */
public class ContactMediumDTO {

    private int id;
    private String name;
    private String value;

    public ContactMediumDTO(){

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