package com.glamasw.petitamirestapi.services;

import java.util.List;

/**
 * ObjectService
 */

public interface GenericService <T> {

    public List<T> findAll() throws Exception;

    public T findById(int id) throws Exception;

    public T save(T t) throws Exception;

    public T update(T t, int id) throws Exception;

    public boolean delete(int id) throws Exception;
}