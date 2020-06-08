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
public class PetController implements GenericController {

    @Autowired
    PetService service;

    @Override
    @CrossOrigin("*")
    @GetMapping(path = "/")
    @Transactional
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
        } catch (Exception e) {
            System.out.println("Error en la base de datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Error en la base de datos\"}");
        }
    }

    @Override
    @GetMapping(path = "/{id}")
    @Transactional
    public ResponseEntity getOne(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
        } catch (Exception e) {
            System.out.println("El id no existe en la base de datos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body("{\"message\": \"Error: El ID ingresado no es válido\"}");
        }
    }

    @GetMapping(path = "/uuid/{uuid}")
    @Transactional
    public ResponseEntity getOneByUuid(@PathVariable String uuid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.findByUuid(uuid));
        } catch (Exception e) {
            System.out.println("El uuid ingresado no existe en la base de datos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body("{\"message\": \"Error: El UUID ingresado no es válido\"}");
        }
    }

    @Override
    @PostMapping(path = "/")
    @Transactional
    public ResponseEntity post(@RequestBody PetDTO petDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.save(petDTO));
        } catch (Exception e) {
            System.out.println("Controlar los datos ingresados e intentar luego");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @Override
    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity put(PetDTO petDTO, int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.update(petDTO, id));
        } catch (Exception e) {
            System.out.println("");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @Override
    @DeleteMapping(path = "/{id}")
    @Transactional
    public ResponseEntity delete(int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.delete(id));
        } catch (Exception e) {
            System.out.println("No existe el id que desea eliminar");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Error: No existe mascota asociada al UUID ingresado\"}");
        }
    }
}
