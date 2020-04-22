package com.glamasw.petitamirestapi.services;

import java.util.*;

import com.glamasw.petitamirestapi.dtos.ContactMediumDTO;
import com.glamasw.petitamirestapi.dtos.DogDTO;
import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Dog;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.DogRepository;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DogService implements GenericService<DogDTO> {

    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    @Transactional
    public List<DogDTO> findAll() throws Exception {

        DogDTO dogDTO = new DogDTO();
        ContactMediumDTO cmDTO = new ContactMediumDTO();
        List<DogDTO> dogDTOs = new ArrayList<>();
        List<ContactMediumDTO> cmDTOs = new ArrayList<>();
        try {
            List<Dog> dogEntities = dogRepository.findAll();

            for (Dog dog : dogEntities) {
                dogDTO.setDogId(dog.getId());
                dogDTO.setDogName(dog.getName());
                dogDTO.setOwnerId(dog.getOwner().getId());
                dogDTO.setOwnerName(dog.getOwner().getName());
                dogDTO.setOwnerDNI(dog.getOwner().getDni());
                for (ContactMedium cm : dog.getOwner().getContactMediums()) {
                    cmDTO.setName(cm.getType());
                    cmDTO.setValue(cm.getValue());
                    cmDTOs.add(cmDTO);
                }
                dogDTO.setContactMediumDTOS(cmDTOs);
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
        Optional<Dog> dogOptional = dogRepository.findById(id);
        Dog dogEntity = new Dog();
        DogDTO dogDTO = new DogDTO();
        ContactMediumDTO cmDTO = new ContactMediumDTO();
        List<ContactMediumDTO> cmDTOs = new ArrayList<>();

        try {
            dogEntity = dogOptional.get();
            dogDTO.setDogId(dogEntity.getId());
            dogDTO.setDogName(dogEntity.getName());
            dogDTO.setOwnerId(dogEntity.getOwner().getId());
            dogDTO.setOwnerName(dogEntity.getOwner().getName());
            dogDTO.setOwnerDNI(dogEntity.getOwner().getDni());
            for (ContactMedium cm : dogEntity.getOwner().getContactMediums()) {
                cmDTO.setName(cm.getType());
                cmDTO.setValue(cm.getValue());
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

        try {
            //Entities init
            Dog dogEntity = new Dog();
            Owner ownerEntity = new Owner();
            List<ContactMedium> contactMediums = new ArrayList<>();
            //Seteo de los atributos del Dog.
            dogEntity.setName(dogDTO.getDogName());
            //Si el id del Owner es igual a 0, este no existe. Se crea.
            if (dogDTO.getOwnerId() ==0) {
                //Seteo de los atributos del Owner.
                ownerEntity.setName(dogDTO.getDogName());
                ownerEntity.setDni(dogDTO.getOwnerDNI());
                //Creación de los ContactMediums a partir del DTO.
                for (ContactMediumDTO contactMediumDTO : dogDTO.getContactMediumDTOS()) {
                    ContactMedium contactMediumEntity = new ContactMedium();
                    contactMediumEntity.setType(contactMediumDTO.getName());
                    contactMediumEntity.setValue(contactMediumDTO.getValue());
                    contactMediums.add(contactMediumEntity);
                }
                ownerEntity.setContactMediums(contactMediums);
                //Asociación bidireccional entre Dog y Owner.
                ownerEntity.getDogs().add(dogEntity);
                dogEntity.setOwner(ownerEntity);
                //Se persiste el Dog, tambien el Owner en cascada.
                dogEntity = dogRepository.save(dogEntity);
                //Se actualizan los ids en el DTO.
                dogDTO.setDogId(dogEntity.getId());
                dogDTO.setOwnerId(dogEntity.getOwner().getId());

            } else { //Si el id es distinto de 0, el Owner existe. Se lo recupera de la DB.
                Optional<Owner> optionalOwner = ownerRepository.findById(dogDTO.getOwnerId());
                ownerEntity = optionalOwner.get();
                //Actualización de los atributos del Owner.
                ownerEntity.setName(dogDTO.getDogName());
                ownerEntity.setDni(dogDTO.getOwnerDNI());
                //Actualización de los ContactMedium
                contactMediums = ownerEntity.getContactMediums();
                contactMediums.clear(); //Se vacía la lista
                //Por cada ContactMediumDTO, guardamos los nuevos (id==0) y actualizamos los existentes (id!=)
                for (ContactMediumDTO contactMediumDTO: dogDTO.getContactMediumDTOS()) {
                    ContactMedium contactMedium = new ContactMedium();
                    if (contactMediumDTO.getId() != 0) {        //Si el id no es 0, lo seteamos, sino lo dejamos vacío para que se genere al momento de guardarse.
                        contactMedium.setId(contactMediumDTO.getId());
                    }
                    contactMedium.setType(contactMediumDTO.getName());
                    contactMedium.setValue(contactMediumDTO.getValue());
                    contactMediums.add(contactMedium);

                    /*Condicional para el caso en el que haya que discriminar entre guardar los nuevos y actualizar los existentes.
                    if (contactMediumDTO.getId() == 0) {
                        contactMedium.setName(contactMediumDTO.getName());
                        contactMedium.setValue(contactMediumDTO.getValue());
                        contactMediums.add(contactMedium);
                    }
                    if (contactMediumDTO != 0) {

                    }*/
                }
            }
            return dogDTO;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    @Override
    @Transactional
    public DogDTO update(DogDTO dogDTO, int id) throws Exception {
        Optional<Dog> dOptional = dogRepository.findById(id);
        try {
            Dog dogEntity = dOptional.get();
            Owner ownerEntity = dogEntity.getOwner();
            ContactMedium cm = new ContactMedium();
            List<ContactMedium> cms = new ArrayList<>();
            dogEntity.setId(dogDTO.getDogId());
            dogEntity.setName(dogDTO.getDogName());
            ownerEntity.setId(dogDTO.getOwnerId());
            ownerEntity.setName(dogDTO.getDogName());
            ownerEntity.setDni(dogDTO.getOwnerDNI());
            ownerEntity.getDogs().add(dogEntity);
            for (ContactMediumDTO cmDTO : dogDTO.getContactMediumDTOS()) {
                cm.setType(cmDTO.getName());
                cm.setValue(cmDTO.getValue());
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
            if (dogRepository.existsById(id)) {
                dogRepository.deleteById(id);
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
