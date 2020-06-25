package com.glamasw.petitamirestapi.services;

import com.glamasw.petitamirestapi.dtos.ContactMediumDTO;
import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.ContactMediumRepository;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContactMediumService implements GenericService<ContactMediumDTO>{

    @Autowired
    ContactMediumRepository contactMediumRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Override
    @Transactional
    public List<ContactMediumDTO> findAll() throws Exception {
        List<ContactMediumDTO> contactMediumDTOS = new ArrayList<>();
        try {
            List<ContactMedium> contactMediums = contactMediumRepository.findAll();
            for (ContactMedium contactMediumEntity : contactMediums) {
                ContactMediumDTO contactMediumDTO = new ContactMediumDTO();
                contactMediumDTO.setId(contactMediumEntity.getId());
                contactMediumDTO.setType(contactMediumEntity.getType());
                contactMediumDTO.setValue(contactMediumEntity.getValue());
                contactMediumDTO.setOwnerId(contactMediumEntity.getOwner().getId());
                contactMediumDTOS.add(contactMediumDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return contactMediumDTOS;
    }

    @Override
    @Transactional
    public ContactMediumDTO findById(int id) throws Exception {
        Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(id);
        ContactMedium contactMediumEntity;
        ContactMediumDTO contactMediumDTO = new ContactMediumDTO();
        try {
            contactMediumEntity = optionalContactMedium.get();
            contactMediumDTO.setId(contactMediumEntity.getId());
            contactMediumDTO.setType(contactMediumEntity.getType());
            contactMediumDTO.setValue(contactMediumEntity.getValue());
            contactMediumDTO.setOwnerId(contactMediumEntity.getOwner().getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return contactMediumDTO;
    }

    @Override
    @Transactional
    public ContactMediumDTO save(ContactMediumDTO contactMediumDTO) throws Exception {
        //Creation of ContactMedium entity
        ContactMedium contactMediumEntity = new ContactMedium();
        contactMediumEntity.setType(contactMediumDTO.getType());
        contactMediumEntity.setValue(contactMediumDTO.getValue());
        try {
            //Persistence of ContactMedium entity
            Optional<Owner> optionalOwner = ownerRepository.findById(contactMediumDTO.getOwnerId());
            Owner ownerEntity = optionalOwner.get();
            ownerEntity.addContactMedium(contactMediumEntity);
            ownerRepository.flush();
            //DTO id update
            contactMediumDTO.setId(contactMediumEntity.getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return contactMediumDTO;
    }

    @Override
    @Transactional
    public ContactMediumDTO update(ContactMediumDTO contactMediumDTO, int id) throws Exception {
        try {
            Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(id);
            ContactMedium contactMediumEntity = optionalContactMedium.get();
            contactMediumEntity.setType(contactMediumDTO.getType());
            contactMediumEntity.setValue(contactMediumDTO.getValue());
            contactMediumRepository.flush();
            // Seteamos el id en el petDto para asegurar de devolver el estado definitivo del ContactMedium (tener en cuenta, por ejemplo, que cualquiera podría enviar
            // un json con id=0 y el request igual se procesaría, porque al id lo tomamos del path de la URI, por lo que debería ser actualizado en el contactMediumDto).
            contactMediumDTO.setId(contactMediumEntity.getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return contactMediumDTO;
    }

    @Override
    public boolean delete(int id) throws Exception {
        try {
            Optional<ContactMedium> optionalContactMedium = contactMediumRepository.findById(id);
            if (optionalContactMedium.isPresent()) {
                ContactMedium contactMediumEntity = optionalContactMedium.get();
                contactMediumEntity.getOwner().removeContactMedium(contactMediumEntity);
                contactMediumRepository.flush();
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
