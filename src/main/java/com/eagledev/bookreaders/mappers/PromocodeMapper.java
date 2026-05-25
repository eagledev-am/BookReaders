package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.commerce.PromocodeDto;
import com.eagledev.bookreaders.entities.Promocode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PromocodeMapper {
    PromocodeDto toDto(Promocode promocode);
    List<PromocodeDto> toDtos(List<Promocode> promocodes);
}

