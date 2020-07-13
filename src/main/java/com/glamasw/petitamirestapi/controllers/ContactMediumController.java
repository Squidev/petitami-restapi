package com.glamasw.petitamirestapi.controllers;

import javax.persistence.Id;
import javax.transaction.Transactional;

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
    @Transactional
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Fallo interno en el servicio\"}");
        }
    }

    @Override
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity getOne(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: El ID ingresado no es válido\"}");
        }
    }

    @GetMapping("/owner/{id}")
    @Transactional
    public ResponseEntity getByOwnerId(@PathVariable int id){
        try {
            return ResponseEntity.status(HttpStatus.OK)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.findByOwnerId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Revise el Id ingresado\"}");
        }
    }

    @Override
    @PostMapping("/")
    @Transactional
    public ResponseEntity save(@RequestBody ContactMediumDTO tDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.save(tDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @Override
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity update(@RequestBody ContactMediumDTO tDTO, @PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                 .body(contactMediumService.update(tDTO, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }

    @Override
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity delete(@PathVariable int id) {
        try {
            contactMediumService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"El registro ha sido eliminado con éxito\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                                 .body("{\"message\":\"Error: Controlar que los datos ingresados sean válidos e intentar luego nuevamente\"}");
        }
    }
}
