package com.salleo.service;

import com.salleo.entity.RoomType;
import com.salleo.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeService extends BaseCrudService<RoomType> {
    public RoomTypeService(RoomTypeRepository repo) { super(repo); }
}

