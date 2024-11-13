package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.Entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface RoomMapper {
    @Mapping(target = "price", source = "price")
    @Mapping(target = "area", source = "area")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "district", source = "district")
    @Mapping(target = "ward", source = "ward")
    RoomDTO toDto(Room room);
}