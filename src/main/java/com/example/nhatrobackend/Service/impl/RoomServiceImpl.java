package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.Entity.Room;
import com.example.nhatrobackend.Responsitory.RoomRepository;
import com.example.nhatrobackend.Service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    @Override
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }
}
