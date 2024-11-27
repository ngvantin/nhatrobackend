package com.example.nhatrobackend.Mapper.Administrative;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.ProvinceDTO;
import com.example.nhatrobackend.Entity.Administrative.Province;
import com.example.nhatrobackend.Mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProvinceMapper extends EntityMapper<ProvinceDTO, Province> {
}
