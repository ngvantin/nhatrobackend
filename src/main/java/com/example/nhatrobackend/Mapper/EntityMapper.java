package com.example.nhatrobackend.Mapper;

import java.util.List;

public interface EntityMapper<D,E> {
    D toDTO(E e);
    E toEntity(D d);
}
