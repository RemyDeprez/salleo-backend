package com.salleo.service;

import com.salleo.entity.Building;
import com.salleo.repository.BuildingRepository;
import org.springframework.stereotype.Service;

@Service
public class BuildingService extends BaseCrudService<Building> {
    public BuildingService(BuildingRepository repo) { super(repo); }
}

