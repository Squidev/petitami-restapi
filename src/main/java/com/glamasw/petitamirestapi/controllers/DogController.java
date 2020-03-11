package com.glamasw.petitamirestapi.controllers;

import java.util.List;

import com.glamasw.petitamirestapi.dtos.DogDTO;
import com.glamasw.petitamirestapi.services.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/dog")
public class DogController implements GenericController {

    @Autowired
    DogService service;

    @Override
    public ResponseEntity getOne(int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
        } catch (Exception e) {
            System.out.println("El id no existe en la base de datos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Revisar el id ingresado.\"}");
        }
    }

    @Override
    public ResponseEntity post(DogDTO dogDTO) {

        return null;
    }

    @Override
    @CrossOrigin("*")
    @GetMapping(path = "/")
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
    public ResponseEntity put(DogDTO dogDTO, int id) {

        return null;
    }

    @Override
    public ResponseEntity delete(int id) {

        return null;
    }
}
