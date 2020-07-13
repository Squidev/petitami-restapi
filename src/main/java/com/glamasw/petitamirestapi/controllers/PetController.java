package com.glamasw.petitamirestapi.controllers;

import com.glamasw.petitamirestapi.dtos.PetDTO;
import com.glamasw.petitamirestapi.services.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/pet")
public class PetController implements GenericController<PetDTO> {

    @Autowired
    PetService petService;

    @Override
    @CrossOrigin("*")
    @GetMapping(path = "/")
    @Transactional
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(petService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Fallo interno en el servicio\"}");
        }
    }

    @Override
    @GetMapping(path = "/{id}")
    @Transactional
    public ResponseEntity getOne(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(petService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: El ID ingresado no es válido\"}");
        }
    }

    @GetMapping(path = "/uuid/{uuid}")
    @Transactional
    public ResponseEntity getOneByUuid(@PathVariable String uuid) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(petService.findByUuid(uuid));
        } catch (Exception e) {
            System.out.println("El uuid ingresado no existe en la base de datos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: El UUID ingresado no es válido\"}");
        }
    }

    @GetMapping(path = "/owner/{id}")
    @Transactional
    public ResponseEntity getByOwnerId(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(petService.findByOwnerId(id));
        } catch (Exception e) {
            System.out.println("El id ingresado no existe en la base de datos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: El ID ingresado no es válido\"}");
        }
    }

    @Override
    @PostMapping(path = "/")
    @Transactional
    public ResponseEntity save(@RequestBody PetDTO petDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(petService.save(petDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity update(@RequestBody PetDTO petDTO, @PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(petService.update(petDTO, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @Override
    @DeleteMapping(path = "/{id}")
    @Transactional
    public ResponseEntity delete(@PathVariable int id) {
        try {
            petService.delete(id);
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"El registro ha sido eliminado con éxito\"}");
        } catch (Exception e) {
            System.out.println("No existe el id que desea eliminar");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }
}
