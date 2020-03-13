package com.glamasw.petitamirestapi.dtos;

import java.util.List;


public class DogDTO {

    private int dogId;
    private String dogName;
//  private byte[] photo;
//  private String description;
    private int ownerId;
    private String ownerName;
    private int ownerDNI;
    private List<ContactMediumDTO> contactMediumDTOS;

    public DogDTO() {

    }

    public int getDogId() {
        return dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
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

    public List<ContactMediumDTO> getContactMediumDTOS() {
        return contactMediumDTOS;
    }

    public void setContactMediumDTOS(List<ContactMediumDTO> contactMediumDTOS) {
        this.contactMediumDTOS = contactMediumDTOS;
    }
}
