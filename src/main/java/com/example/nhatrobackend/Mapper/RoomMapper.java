package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.PostRequestDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.Room;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface RoomMapper {

    Room toRoom(PostRequestDTO postRequestDTO);
}
