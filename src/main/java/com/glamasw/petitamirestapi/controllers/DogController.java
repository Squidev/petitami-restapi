package com.glamasw.petitamirestapi.controllers;

import java.util.List;

import com.glamasw.petitamirestapi.dtos.DogDTO;
import com.glamasw.petitamirestapi.services.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/dog")
public class DogController implements GenericController {

    @Autowired
    DogService service;

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
    public ResponseEntity getOne(int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
        } catch (Exception e) {
            System.out.println("El id no existe en la base de datos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Revisar el id ingresado.\"}");
        }
    }

    @Override
    @PostMapping(path = "/")
    @Transactional
    public ResponseEntity post(DogDTO dogDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.save(dogDTO));
        } catch (Exception e) {
            System.out.println("Controlar los datos ingresados e intentar luego");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Controlar los datos ingresados e intentar luego nuevamente.\"}");
        }
    }

    @Override
    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity put(DogDTO dogDTO, int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.update(dogDTO, id));
        } catch (Exception e) {
            System.out.println("");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Controlar los datos ingresados e intentar luego nuevamente\"}");
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
                    .body("{\"message\": \"No existe el id que desea eliminar\"}");
        }
    }
}
