package com.glamasw.petitamirestapi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.glamasw.petitamirestapi.dtos.ContactMediumDTO;
import com.glamasw.petitamirestapi.dtos.DogDTO;
import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Dog;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DogService implements GenericService<DogDTO> {

    @Autowired
    DogRepository repository;

    @Override
    public List<DogDTO> findAll() throws Exception {

        DogDTO dogDTO = new DogDTO();
        ContactMediumDTO cmDTO = new ContactMediumDTO();
        List<DogDTO> dogDTOs = new ArrayList<DogDTO>();
        List<ContactMediumDTO> cmDTOs = new ArrayList<ContactMediumDTO>();
        try {
            List<Dog> dogEntities = repository.findAll();

            for (Dog dog : dogEntities) {
                dogDTO.setId(dog.getId());
                dogDTO.setName(dog.getName());
                dogDTO.setOwnerId(dog.getOwner().getId());
                dogDTO.setOwnerName(dog.getOwner().getName());
                dogDTO.setOwnerDNI(dog.getOwner().getDni());
                for (ContactMedium cm : dog.getOwner().getContacts()) {
                    cmDTO.setNombre(cm.getNombre());
                    cmDTO.setValor(cm.getValor());
                    cmDTOs.add(cmDTO);
                }
                dogDTO.setContacts(cmDTOs);
                dogDTOs.add(dogDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return dogDTOs;
    }

    @Override
    public DogDTO findById(int id) throws Exception {
        Optional<Dog> dogOptional = repository.findById(id);
        Dog dogEntity = new Dog();
        DogDTO dogDTO = new DogDTO();

        try {
            dogEntity = dogOptional.get();
            dogDTO.setId(dogEntity.getId());
            dogDTO.setName(dogEntity.getName());
            dogDTO.setOwnerId(dogEntity.getOwner().getId());
            dogDTO.setOwnerName(dogEntity.getOwner().getName());
            dogDTO.setOwnerDNI(dogEntity.getOwner().getDni());
            dogDTO.setContacts(dogEntity.getOwner().getContacts());
        } catch (Exception e) {
            throw new Exception();
        }

        return null;
    }

    @Override
    public DogDTO save(DogDTO dogDTO) {
        Dog dogEntity = new Dog();
        Owner ownerEntity = new Owner();
        ContactMedium contactEntity = new ContactMedium();
        dogEntity.setName(dogDTO.getName());
        ownerEntity.setName(dogDTO.getOwner().getName());

        try {

        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public DogDTO update(DogDTO t, int id) {

        return null;
    }

    @Override
    public boolean delete() {

        return false;
    }

}
