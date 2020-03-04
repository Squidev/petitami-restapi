package com.glamasw.petitamirestapi.controllers;

import com.glamasw.petitamirestapi.services.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DogController {

    @Autowired
    DogService service;


}
