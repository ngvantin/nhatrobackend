package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.PostRequestDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.DTO.RoomDTO;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface RoomMapper {

    Room toRoom(PostRequestDTO postRequestDTO);

    void updateRoomFromDTO(PostRequestDTO dto, @MappingTarget Room room);
    // Chuyển đổi từ Room sang RoomDTO
    RoomDTO toRoomDTO(Room room);

}
