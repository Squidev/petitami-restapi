package com.glamasw.petitamirestapi.services;

import com.glamasw.petitamirestapi.repositories.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
public class DogService {

    @Autowired
    DogRepository repository;
}
