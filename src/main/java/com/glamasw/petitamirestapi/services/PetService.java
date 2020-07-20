package com.glamasw.petitamirestapi.services;

import java.util.*;
import com.glamasw.petitamirestapi.dtos.PetDTO;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import com.glamasw.petitamirestapi.repositories.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService implements GenericService<PetDTO> {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    @Transactional
    public List<PetDTO> findAll() throws Exception {

        List<PetDTO> petDTOs = new ArrayList<>();
        try {
            List<Pet> petEntities = petRepository.findAll();
            for (Pet petEntity : petEntities) {
                PetDTO petDTO = new PetDTO();
                petDTO.setId(petEntity.getId());
                petDTO.setUuid(petEntity.getUuid());
                petDTO.setName(petEntity.getName());
                petDTO.setPhoto(petEntity.getPhoto());
                petDTO.setDescription(petEntity.getDescription());
                petDTO.setOwnerId(petEntity.getOwner().getId());
                petDTOs.add(petDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTOs;
    }

    @Override
    @Transactional
    public PetDTO findById(int id) throws Exception {
        Optional<Pet> optionalPet = petRepository.findById(id);
        Pet petEntity;
        PetDTO petDTO = new PetDTO();
        try {
            petEntity = optionalPet.get();
            petDTO.setId(petEntity.getId());
            petDTO.setUuid(petEntity.getUuid());
            petDTO.setName(petEntity.getName());
            petDTO.setPhoto(petEntity.getPhoto());
            petDTO.setDescription(petEntity.getDescription());
            petDTO.setOwnerId(petEntity.getOwner().getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTO;
    }

    @Transactional
    public PetDTO findByUuid(String uuid) throws Exception {
        Optional<Pet> optionalPet = petRepository.findByUuid(uuid);
        Pet petEntity;
        PetDTO petDTO = new PetDTO();
        try {
            petEntity = optionalPet.get();
            petDTO.setId(petEntity.getId());
            petDTO.setUuid(petEntity.getUuid());
            petDTO.setName(petEntity.getName());
            petDTO.setPhoto(petEntity.getPhoto());
            petDTO.setDescription(petEntity.getDescription());
            petDTO.setOwnerId(petEntity.getOwner().getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTO;
    }

    @Transactional
    public List<PetDTO> findByOwnerId(int id) throws Exception {
        List<PetDTO> petDTOs = new ArrayList<>();
        try {
            Optional<Owner> optionalOwner = ownerRepository.findById(id);
            Owner ownerEntity = optionalOwner.get();
            for (Pet petEntity : ownerEntity.getPets()) {
                PetDTO petDTO = new PetDTO();
                petDTO.setId(petEntity.getId());
                petDTO.setUuid(petEntity.getUuid());
                petDTO.setName(petEntity.getName());
                petDTO.setPhoto(petEntity.getPhoto());
                petDTO.setDescription(petEntity.getDescription());
                petDTO.setOwnerId(petEntity.getOwner().getId());
                petDTOs.add(petDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTOs;
    }

    @Override
    @Transactional
    public PetDTO save(PetDTO petDTO) throws Exception {
        //Creation of Pet entity
        Pet petEntity = new Pet();
        petEntity.setName(petDTO.getName());
        petEntity.setPhoto(petDTO.getPhoto());
        petEntity.setDescription(petDTO.getDescription());
        try {
            //Persistence of Pet entity
            Optional<Owner> optionalOwner = ownerRepository.findById(petDTO.getOwnerId());
            Owner ownerEntity = optionalOwner.get();
            ownerEntity.addPet(petEntity);
            ownerRepository.flush();
            //DTO ID and UUID update
            petDTO.setId(petEntity.getId());
            petDTO.setUuid(petEntity.getUuid());
            return petDTO;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    @Override
    @Transactional
    public PetDTO update(PetDTO petDTO, int id) throws Exception {
        try {
            Optional<Pet> optionalPet = petRepository.findById(id);
            Pet petEntity = optionalPet.get();
            //id, uuid y ownerId no serán propiedades editables, por lo que las ignoramos
            petEntity.setName(petDTO.getName());
            petEntity.setPhoto(petDTO.getPhoto());
            petEntity.setDescription(petDTO.getDescription());
            petRepository.flush();
            //De cualquier manera setearemos esas 3 propiedades en el petDto para asegurar de devolver el estado definitivo de la Pet (tener en cuenta, por ejemplo, que
            // cualquiera podría enviar un json con id=0 y el request igual se procesaría, porque al id lo tomamos del path de la URI, por lo que debería ser
            // actualizado en el petDto).
            petDTO.setId(petEntity.getId());
            petDTO.setUuid(petEntity.getUuid());
            petDTO.setOwnerId(petEntity.getOwner().getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTO;
    }

    @Override
    @Transactional
    public boolean delete(int id) throws Exception {
        try {
            /*Podemos deletear la Pet de 2 maneras:
            1. Recuperando al respectivo Owner, removiendo a la Pet de la lista, y flusheando los cambios.
            2. Deleteando directamente la Pet SÓLO si el respectivo Owner no se encuentra instanciado. De lo contrario, el contexto de persistencia encontrará una
            instrucción de deleteo de la Pet, también una instrucción de savearla (al estar actualmente asociada a un Owner instanciado) y como resultado se
            considerará que no hay ningún cambio neto y no se scheduleará el deleteo al momento del flush.
            A modo de verificación, a continuación se utiliza el segundo método, y utilizamos el primero en la implementación del delete() de ContactMedium.
            */
            if (petRepository.existsById(id)) {
                petRepository.deleteById(id);
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
