package com.glamasw.petitamirestapi.dtos;

import java.util.List;

import javax.swing.ImageIcon;

import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;

public class DogDTO {

    private int id;
    private String name;
    private ImageIcon image;
    private String description;
    private int ownerId;
    private String ownerName;
    private int ownerDNI;
    private List<ContactMediumDTO> contacts;

    public DogDTO() {

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

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getOwnerDNI() {
        return ownerDNI;
    }

    public void setOwnerDNI(int ownerDNI) {
        this.ownerDNI = ownerDNI;
    }

    public List<ContactMediumDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactMediumDTO> contacts) {
        this.contacts = contacts;
    }





    

    
}
