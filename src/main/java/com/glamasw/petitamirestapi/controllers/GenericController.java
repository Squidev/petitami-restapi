package com.glamasw.petitamirestapi.controllers;

import com.glamasw.petitamirestapi.dtos.DogDTO;

import org.springframework.http.ResponseEntity;

/**
 * ObjectController
 */

public interface GenericController {

    public ResponseEntity getAll();

    public ResponseEntity getOne(int id);

    public ResponseEntity post(DogDTO dogDTO);

    public ResponseEntity put(DogDTO dogDTO, int id);

    public ResponseEntity delete(int id);
}