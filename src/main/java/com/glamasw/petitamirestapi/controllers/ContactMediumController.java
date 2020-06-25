package com.glamasw.petitamirestapi.controllers;

import com.glamasw.petitamirestapi.dtos.ContactMediumDTO;
import com.glamasw.petitamirestapi.services.ContactMediumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contactmedium")
@CrossOrigin("*")
public class ContactMediumController implements GenericController<ContactMediumDTO> {

    @Autowired
    ContactMediumService contactMediumService;

    @Override
    @GetMapping("/")
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Fallo interno en el servicio\"}");
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity getOne(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: El ID ingresado no es válido\"}");
        }
    }

    @Override
    @PostMapping("/")
    public ResponseEntity save(@RequestBody ContactMediumDTO tDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.save(tDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody ContactMediumDTO tDTO, @PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.update(tDTO, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        try {
            contactMediumService.delete(id);
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"El registro ha sido eliminado con éxito\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }
}
