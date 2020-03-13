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
import org.springframework.transaction.annotation.Transactional;

@Service
public class DogService implements GenericService<DogDTO> {

    @Autowired
    DogRepository repository;

    @Override
    @Transactional
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
                for (ContactMedium cm : dog.getOwner().getContactMediums()) {
                    cmDTO.setNombre(cm.getName());
                    cmDTO.setValor(cm.getValue());
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
    @Transactional
    public DogDTO findById(int id) throws Exception {
        Optional<Dog> dogOptional = repository.findById(id);
        Dog dogEntity = new Dog();
        DogDTO dogDTO = new DogDTO();
        ContactMediumDTO cmDTO = new ContactMediumDTO();
        List<ContactMediumDTO> cmDTOs = new ArrayList<ContactMediumDTO>();

        try {
            dogEntity = dogOptional.get();
            dogDTO.setId(dogEntity.getId());
            dogDTO.setName(dogEntity.getName());
            dogDTO.setOwnerId(dogEntity.getOwner().getId());
            dogDTO.setOwnerName(dogEntity.getOwner().getName());
            dogDTO.setOwnerDNI(dogEntity.getOwner().getDni());
            for (ContactMedium cm : dogEntity.getOwner().getContactMediums()) {
                cmDTO.setNombre(cm.getName());
                cmDTO.setValor(cm.getValue());
                cmDTOs.add(cmDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }

        return dogDTO;
    }

    @Override
    @Transactional
    public DogDTO save(DogDTO dogDTO) throws Exception {
        Dog dogEntity = new Dog();
        Owner ownerEntity = new Owner();
        ContactMedium cm = new ContactMedium();
        List<ContactMedium> cms = new ArrayList<ContactMedium>();
        dogEntity.setName(dogDTO.getName());
        dogEntity.setPhoto(dogDTO.getPhoto());
        ownerEntity.setName(dogDTO.getName());
        ownerEntity.setDni(dogDTO.getOwnerDNI());
        ownerEntity.getDogs().add(dogEntity);
        for (ContactMediumDTO cmDTO : dogDTO.getContacts()) {
            cm.setName(cmDTO.getNombre());
            cm.setValue(cmDTO.getValor());
            cms.add(cm);
        }

        try {
            repository.save(dogEntity);
            dogDTO.setId(dogEntity.getId());
            dogDTO.setOwnerId(dogEntity.getOwner().getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return dogDTO;
    }

    @Override
    @Transactional
    public DogDTO update(DogDTO dogDTO, int id) throws Exception {
        Optional<Dog> dOptional = repository.findById(id);
        try {
            Dog dogEntity = dOptional.get();
            Owner ownerEntity = dogEntity.getOwner();
            ContactMedium cm = new ContactMedium();
            List<ContactMedium> cms = new ArrayList<ContactMedium>();
            dogEntity.setId(dogDTO.getId());
            dogEntity.setName(dogDTO.getName());
            ownerEntity.setId(dogDTO.getOwnerId());
            ownerEntity.setName(dogDTO.getName());
            ownerEntity.setDni(dogDTO.getOwnerDNI());
            ownerEntity.getDogs().add(dogEntity);
            for (ContactMediumDTO cmDTO : dogDTO.getContacts()) {
                cm.setName(cmDTO.getNombre());
                cm.setValue(cmDTO.getValor());
                cms.add(cm);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return dogDTO;
    }

    @Override
    @Transactional
    public boolean delete(int id) throws Exception {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

}
