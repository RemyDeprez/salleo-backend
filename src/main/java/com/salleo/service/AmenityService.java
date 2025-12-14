package com.salleo.service;

import com.salleo.entity.Amenity;
import com.salleo.repository.AmenityRepository;
import org.springframework.stereotype.Service;

@Service
public class AmenityService extends BaseCrudService<Amenity> {
    public AmenityService(AmenityRepository repo) { super(repo); }
}

