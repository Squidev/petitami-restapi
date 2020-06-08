package com.glamasw.petitamirestapi.controllers;

import com.glamasw.petitamirestapi.dtos.PetDTO;

import org.springframework.http.ResponseEntity;

/**
 * ObjectController
 */

public interface GenericController {

    ResponseEntity getAll();

    ResponseEntity getOne(int id);

    ResponseEntity post(PetDTO petDTO);

    ResponseEntity put(PetDTO petDTO, int id);

    ResponseEntity delete(int id);
}