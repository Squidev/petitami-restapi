package com.glamasw.petitamirestapi.services;

import com.glamasw.petitamirestapi.repositories.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DogService {

    @Autowired
    DogRepository repository;
}
