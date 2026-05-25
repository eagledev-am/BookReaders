package com.eagledev.bookreaders.services.address;

import com.eagledev.bookreaders.dtos.user.AddressDto;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressDto addAddress(UUID userUuid, AddressDto addressDto);
    AddressDto updateAddress(UUID addressUuid, UUID userUuid, AddressDto addressDto);
    List<AddressDto> getUserAddresses(UUID userUuid);
    AddressDto setDefaultAddress(UUID addressUuid, UUID userUuid);
}

