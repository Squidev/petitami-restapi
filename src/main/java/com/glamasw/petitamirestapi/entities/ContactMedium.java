package com.glamasw.petitamirestapi.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "apirest_medioContacto")
public class ContactMedium {

    private int id;
    private String nombre;
    private String valor;
    
}