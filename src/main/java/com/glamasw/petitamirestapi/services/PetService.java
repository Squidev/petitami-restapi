package com.glamasw.petitamirestapi.services;

import java.util.*;

import com.glamasw.petitamirestapi.dtos.ContactMediumDTO;
import com.glamasw.petitamirestapi.dtos.PetDTO;
import com.glamasw.petitamirestapi.entities.ContactMedium;
import com.glamasw.petitamirestapi.entities.Pet;
import com.glamasw.petitamirestapi.entities.Owner;
import com.glamasw.petitamirestapi.repositories.ContactMediumRepository;
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
    @Autowired
    private ContactMediumRepository contactMediumRepository;

    @Override
    @Transactional
    public List<PetDTO> findAll() throws Exception {

        List<PetDTO> petDTOs = new ArrayList<>();
        try {
            List<Pet> petEntities = petRepository.findAll();

            for (Pet petEntity : petEntities) {
                PetDTO petDTO = new PetDTO();
                petDTO.setPetId(petEntity.getId());
                petDTO.setPetUuid(petEntity.getUuid());
                petDTO.setPetName(petEntity.getName());
                petDTO.setPetPhoto(petEntity.getPhoto());
                petDTO.setPetDescription(petEntity.getDescription());
                petDTO.setOwnerId(petEntity.getOwner().getId());
                petDTO.setOwnerDni(petEntity.getOwner().getDni());
                petDTO.setOwnerName(petEntity.getOwner().getName());
                for (ContactMedium cm : petEntity.getOwner().getContactMediums()) {
                    ContactMediumDTO cmDTO = new ContactMediumDTO();
                    cmDTO.setId(cm.getId());
                    cmDTO.setType(cm.getType());
                    cmDTO.setValue(cm.getValue());
                    petDTO.addContactMediumDTO(cmDTO);
                }
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
        Optional<Pet> dogOptional = petRepository.findById(id);
        Pet petEntity;
        PetDTO petDTO = new PetDTO();
        try {
            petEntity = dogOptional.get();
            petDTO.setPetId(petEntity.getId());
            petDTO.setPetUuid(petEntity.getUuid());
            petDTO.setPetName(petEntity.getName());
            petDTO.setPetPhoto(petEntity.getPhoto());
            petDTO.setPetDescription(petEntity.getDescription());
            petDTO.setOwnerId(petEntity.getOwner().getId());
            petDTO.setOwnerDni(petEntity.getOwner().getDni());
            petDTO.setOwnerName(petEntity.getOwner().getName());
            for (ContactMedium cm : petEntity.getOwner().getContactMediums()) {
                ContactMediumDTO cmDTO = new ContactMediumDTO();
                cmDTO.setId(cm.getId());
                cmDTO.setType(cm.getType());
                cmDTO.setValue(cm.getValue());
                petDTO.addContactMediumDTO(cmDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTO;
    }

    @Transactional
    public PetDTO findByUuid(String uuid) throws Exception {
        Optional<Pet> dogOptional = petRepository.findByUuid(uuid);
        Pet petEntity;
        PetDTO petDTO = new PetDTO();
        try {
            petEntity = dogOptional.get();
            petDTO.setPetId(petEntity.getId());
            petDTO.setPetUuid(petEntity.getUuid());
            petDTO.setPetName(petEntity.getName());
            petDTO.setPetPhoto(petEntity.getPhoto());
            petDTO.setPetDescription(petEntity.getDescription());
            petDTO.setOwnerId(petEntity.getOwner().getId());
            petDTO.setOwnerDni(petEntity.getOwner().getDni());
            petDTO.setOwnerName(petEntity.getOwner().getName());
            for (ContactMedium cm : petEntity.getOwner().getContactMediums()) {
                ContactMediumDTO cmDTO = new ContactMediumDTO();
                cmDTO.setId(cm.getId());
                cmDTO.setType(cm.getType());
                cmDTO.setValue(cm.getValue());
                petDTO.addContactMediumDTO(cmDTO);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTO;
    }

    @Override
    @Transactional
    public PetDTO save(PetDTO petDTO) throws Exception {

        try {
            //Entities init
            Owner ownerEntity;
            //Si el id del Owner es igual a 0, este no existe. Se crea.
            if (petDTO.getOwnerId() ==0) {
                //Creation of Owner
                ownerEntity = new Owner();
                ownerEntity.setDni(petDTO.getOwnerDni());
                ownerEntity.setName(petDTO.getPetName());

                //Creation of Pet
                Pet petEntity = new Pet();
                petEntity.setName(petDTO.getPetName());
                petEntity.setPhoto(petDTO.getPetPhoto());
                petEntity.setDescription(petDTO.getPetDescription());
                ownerEntity.addPet(petEntity);

                //Creation of ContactMediums
                for (ContactMediumDTO contactMediumDTO : petDTO.getContactMediumDTOs()) {
                    ContactMedium contactMediumEntity = new ContactMedium();
                    contactMediumEntity.setType(contactMediumDTO.getType());
                    contactMediumEntity.setValue(contactMediumDTO.getValue());
                    ownerEntity.addContactMedium(contactMediumEntity);
                }

                //Se persiste el Owner, también la Pet y ContactMediums debido al cascadeo.
                ownerRepository.save(ownerEntity);

                //Se actualizan los ids generados en el DTO.
                petDTO.setPetId(petEntity.getId());
                petDTO.setOwnerId(ownerEntity.getId());
                for (int i = 0; i < petDTO.getContactMediumDTOs().size(); i++) {
                    petDTO.getContactMediumDTOs().get(i).setId(ownerEntity.getContactMediums().get(i).getId());
                }

            } else { //Si el id es distinto de 0, el Owner existe. Se lo recupera de la DB.
                Optional<Owner> optionalOwner = ownerRepository.findById(petDTO.getOwnerId());
                ownerEntity = optionalOwner.get();

                //Actualización de los atributos del Owner.
                ownerEntity.setDni(petDTO.getOwnerDni());
                ownerEntity.setName(petDTO.getPetName());

                //Actualización de los ContactMedium
                //Por cada ContactMediumDTO, guardamos los nuevos (id==0), actualizamos los existentes (id!=0) y eliminamos aquellos ¿cuyos ids ya no están en el
                // ContactMediumDTO pero sí en la lista ContactMediums del Owner? (Lo cual equivaldría a vaciar la lista y agregar todos los contactMediumDTO recibidos
                // al Owner recuperado)
                ownerEntity.getContactMediums().clear(); //Se vacía la lista
                for (ContactMediumDTO contactMediumDTO : petDTO.getContactMediumDTOs()) {
                    //La siguiente línea no debería ejecutar ninguna instrucción select en la DB, dado que los contactMediumEntity ya deberían estar instanciados en el
                    // contexto de persistencia desde el momento en el que recuperamos el Owner que los incluye anteriormente.
                    ContactMedium contactMediumEntity = new ContactMedium();
                    contactMediumEntity.setId(contactMediumDTO.getId());
                    contactMediumEntity.setType(contactMediumDTO.getType());
                    contactMediumEntity.setValue(contactMediumDTO.getValue());
                    ownerEntity.addContactMedium(contactMediumEntity);
                }
                //Y acá es donde me doy cuenta de que estamos haciendo las cosas pal pingo. De una sola llamada a la API para el save de una Pet estamos
                // triggereando save/update de un Owner y save/update/delete de ContactMediums en una deliciosa y venenosa sopita de condicionales. Al mismo tiempo si,
                // por ejemplo, un Owner ya tuviera 5 ContactMediums y simplemente agregáramos un 6to, los 5 ya existentes se enviarían innecesariamente en la
                // transacción para solo ser ignorados en el backend. Esto se repetiría para el update de cualquier Pet, Owner, ContactMedium, sin tener en cuenta que,
                // en caso de hacer un GetAllPets estaríamos enviando absolutamente toda la información de cada una de ellas (which sounds like an anti-pattern to me)
                // en lugar de sólo recuperar los ContactMedium para la Pet que al admin le interese consultar.
            }
            return petDTO;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    @Override
    @Transactional
    public PetDTO update(PetDTO petDTO, int id) throws Exception {
        Optional<Pet> dOptional = petRepository.findById(id);
        try {
            Pet petEntity = dOptional.get();
            Owner ownerEntity = petEntity.getOwner();
            ContactMedium cm = new ContactMedium();
            List<ContactMedium> cms = new ArrayList<>();
            petEntity.setId(petDTO.getPetId());
            petEntity.setName(petDTO.getPetName());
            ownerEntity.setId(petDTO.getOwnerId());
            ownerEntity.setName(petDTO.getPetName());
            ownerEntity.setDni(petDTO.getOwnerDni());
            ownerEntity.getPets().add(petEntity);
            for (ContactMediumDTO cmDTO : petDTO.getContactMediumDTOs()) {
                cm.setType(cmDTO.getType());
                cm.setValue(cmDTO.getValue());
                cms.add(cm);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return petDTO;
    }

    @Override
    @Transactional
    public boolean delete(int id) throws Exception {
        try {
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
