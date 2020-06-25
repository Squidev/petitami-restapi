package com.glamasw.petitamirestapi.controllers;

import org.springframework.http.ResponseEntity;

/**
 * ObjectController
 */

public interface GenericController<T> {

    ResponseEntity getAll();

    ResponseEntity getOne(int id);

    ResponseEntity save(T tDto);

    ResponseEntity update(T tDTO, int id);

    ResponseEntity delete(int id);
}