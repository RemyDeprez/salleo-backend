package com.salleo.service;

import com.salleo.entity.Room;
import com.salleo.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService extends BaseCrudService<Room> {
    public RoomService(RoomRepository repo) { super(repo); }
}

