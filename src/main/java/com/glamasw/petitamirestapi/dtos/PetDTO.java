package com.glamasw.petitamirestapi.dtos;

import java.util.ArrayList;
import java.util.List;


public class PetDTO {

    private int petId;
    private String petUuid;
    private String petName;
    private byte[] petPhoto;
    private String petDescription;
    private int ownerId;
    private int ownerDni;
    private String ownerName;
    private List<ContactMediumDTO> contactMediumDTOs = new ArrayList<>();

    public PetDTO() {

    }

    //GETTERS AND SETTERS
    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getPetUuid() {
        return petUuid;
    }

    public void setPetUuid(String petUuid) {
        this.petUuid = petUuid;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public byte[] getPetPhoto() {
        return petPhoto;
    }

    public void setPetPhoto(byte[] petPhoto) {
        this.petPhoto = petPhoto;
    }

    public String getPetDescription() {
        return petDescription;
    }

    public void setPetDescription(String petDescription) {
        this.petDescription = petDescription;
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

    public int getOwnerDni() {
        return ownerDni;
    }

    public void setOwnerDni(int ownerDni) {
        this.ownerDni = ownerDni;
    }

    public List<ContactMediumDTO> getContactMediumDTOs() {
        return contactMediumDTOs;
    }

    public void setContactMediumDTOs(List<ContactMediumDTO> contactMediumDTOs) {
        this.contactMediumDTOs = contactMediumDTOs;
    }

    //METHODS
    public void addContactMediumDTO(ContactMediumDTO contactMediumDTO) {
        contactMediumDTOs.add(contactMediumDTO);
    }

    public void deleteContactMediumDTO(ContactMediumDTO contactMediumDTO) {
        contactMediumDTOs.remove(contactMediumDTO);
    }
}
