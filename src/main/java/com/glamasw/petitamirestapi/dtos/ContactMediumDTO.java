package com.glamasw.petitamirestapi.dtos;

/**
 * ContactMediumDTO
 */
public class ContactMediumDTO {

    private int id;
    private String type;
    private String value;

    public ContactMediumDTO(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
}