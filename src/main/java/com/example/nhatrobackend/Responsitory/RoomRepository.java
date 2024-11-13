package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Room;
import com.example.nhatrobackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository  extends JpaRepository<Room, Integer> {
}
