package com.example.nhatrobackend.Mapper.Administrative;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.DistrictDTO;
import com.example.nhatrobackend.Entity.Administrative.District;
import com.example.nhatrobackend.Mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DistrictMapper extends EntityMapper<DistrictDTO, District> {
}
