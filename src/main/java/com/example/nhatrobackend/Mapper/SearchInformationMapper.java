package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.request.SearchInforRequest;
import com.example.nhatrobackend.DTO.response.SearchInforResponse;
import com.example.nhatrobackend.Entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface SearchInformationMapper {

    @Mapping(target = "user", ignore = true) // User sẽ được set riêng
    SearchInformation toSearchInformation(SearchInforRequest dto);

    @Mapping(source = "minPrice", target = "minPrice")
    @Mapping(source = "maxPrice", target = "maxPrice")
    @Mapping(source = "minArea", target = "minArea")
    @Mapping(source = "maxArea", target = "maxArea")
    @Mapping(source = "furnitureStatus", target = "furnitureStatus")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "district", target = "district")
    @Mapping(source = "ward", target = "ward")
    SearchInforRequest toSearchInforRequest(SearchInformation searchInformation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateSearchInformationFromDTO(SearchInforRequest searchInforRequest, @MappingTarget SearchInformation searchInformation);

    SearchInforResponse toSearchInforResponse(SearchInformation searchInformation);
}