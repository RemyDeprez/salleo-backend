package com.salleo.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseCrudService<T> {
    protected final JpaRepository<T, Long> repository;

    protected BaseCrudService(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

