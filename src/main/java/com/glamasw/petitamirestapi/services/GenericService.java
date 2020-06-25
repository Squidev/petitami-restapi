package com.glamasw.petitamirestapi.services;

import java.util.List;

public interface GenericService <T> {

    List<T> findAll() throws Exception;

    T findById(int id) throws Exception;

    T save(T t) throws Exception;

    T update(T t, int id) throws Exception;

    boolean delete(int id) throws Exception;
}