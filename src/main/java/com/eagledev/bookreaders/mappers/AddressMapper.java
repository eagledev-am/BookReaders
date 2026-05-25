package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.user.AddressDto;
import com.eagledev.bookreaders.entities.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    AddressDto addressToDto(Address address);
    List<AddressDto> addressesToDtos(List<Address> addresses);
}

