package com.glamasw.petitamirestapi.services;

import com.glamasw.petitamirestapi.dtos.OwnerDTO;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OwnerService implements GenericService<OwnerDTO> {

    @Autowired
    OwnerRepository ownerRepository;

    @Override
    @Transactional
    public List<OwnerDTO> findAll() throws Exception {
        List<OwnerDTO> ownerDTOS = new ArrayList<>();
        try {
            List<Owner> owners = ownerRepository.findAll();
            for (Owner ownerEntity : owners) {
                OwnerDTO ownerDTO = new OwnerDTO();
                ownerDTO.setId(ownerEntity.getId());
                ownerDTO.setDni(ownerEntity.getDni());
                ownerDTO.setName(ownerEntity.getName());
                ownerDTOS.add(ownerDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return ownerDTOS;
    }
                
    @Override
    @Transactional
    public OwnerDTO findById(int id) throws Exception {
        Optional<Owner> optionalOwner = ownerRepository.findById(id);
        Owner ownerEntity;
        OwnerDTO ownerDTO = new OwnerDTO();
        try {
            ownerEntity = optionalOwner.get();
            ownerDTO.setId(ownerEntity.getId());
            ownerDTO.setDni(ownerEntity.getDni());
            ownerDTO.setName(ownerEntity.getName());
        } catch (Exception e) {
            throw new Exception();
        }
        return ownerDTO;
    }

    @Transactional
    public OwnerDTO findByDni(int dni) throws Exception {
        Optional<Owner> optionalOwner = ownerRepository.findByDni(dni);
        Owner ownerEntity;
        OwnerDTO ownerDTO = new OwnerDTO();
        try {
            ownerEntity = optionalOwner.get();
            ownerDTO.setId(ownerEntity.getId());
            ownerDTO.setDni(ownerEntity.getDni());
            ownerDTO.setName(ownerEntity.getName());
        } catch (Exception e) {
            throw new Exception();
        }
        return ownerDTO;
    }

    @Override
    public OwnerDTO save(OwnerDTO ownerDTO) throws Exception {
        //Creation of Owner entity
        Owner ownerEntity = new Owner();
        ownerEntity.setDni(ownerDTO.getDni());
        ownerEntity.setName(ownerDTO.getName());
        try {
            //Persistence of Owner entity
            ownerRepository.save(ownerEntity);
            //DTO id update
            ownerDTO.setId(ownerEntity.getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return ownerDTO;
    }

    @Override
    public OwnerDTO update(OwnerDTO ownerDTO, int id) throws Exception {
        try {
            Optional<Owner> optionalOwner = ownerRepository.findById(id);
            Owner ownerEntity = optionalOwner.get();
            ownerEntity.setDni(ownerDTO.getDni());
            ownerEntity.setName(ownerDTO.getName());
            ownerRepository.flush();
            //Seteamos el id en el ownerDto para asegurar de devolver el estado definitivo del Owner (tener en cuenta, por ejemplo, que cualquiera podría enviar un
            // json con id=0 y el request igual se procesaría, porque al id lo tomamos del path de la URI, por lo que debería ser actualizado en el ownerDto).
            ownerDTO.setId(ownerEntity.getId());
        } catch (Exception e) {
            throw new Exception();
        }
        return ownerDTO;
    }

    @Override
    public boolean delete(int id) throws Exception {
        try {
            if (ownerRepository.existsById(id)) {
                ownerRepository.deleteById(id);
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
