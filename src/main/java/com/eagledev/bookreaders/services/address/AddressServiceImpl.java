package com.eagledev.bookreaders.services.address;

import com.eagledev.bookreaders.dtos.user.AddressDto;
import com.eagledev.bookreaders.entities.Address;
import com.eagledev.bookreaders.mappers.AddressMapper;
import com.eagledev.bookreaders.repos.AddressRepo;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepo addressRepo;
    private final UserService userService;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressDto addAddress(UUID userUuid, AddressDto addressDto) {
        User user = userService.getUserById(userUuid);

        if (addressDto.isDefault()) {
            addressRepo.findByUserAndIsDefaultTrue(user)
                    .ifPresent(existing -> existing.setDefault(false));
        }

        Address address = Address.builder()
                .user(user)
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .zipCode(addressDto.getZipCode())
                .isDefault(addressDto.isDefault())
                .build();

        Address saved = addressRepo.save(address);
        return addressMapper.addressToDto(saved);
    }

    @Override
    @Transactional
    public AddressDto updateAddress(UUID addressUuid, UUID userUuid, AddressDto addressDto) {
        Address address = addressRepo.findByUuid(addressUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "uuid", addressUuid));

        if (!address.getUser().getUuid().equals(userUuid)) {
            throw new AccessDeniedException("You are not the owner of this address");
        }

        if (addressDto.isDefault() && !address.isDefault()) {
            addressRepo.findByUserAndIsDefaultTrue(address.getUser())
                    .ifPresent(existing -> existing.setDefault(false));
            address.setDefault(true);
        } else if (!addressDto.isDefault()) {
            address.setDefault(false);
        }

        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setZipCode(addressDto.getZipCode());

        Address saved = addressRepo.save(address);
        return addressMapper.addressToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getUserAddresses(UUID userUuid) {
        List<Address> addresses = addressRepo.findByUserUuid(userUuid);
        return addressMapper.addressesToDtos(addresses);
    }

    @Override
    @Transactional
    public AddressDto setDefaultAddress(UUID addressUuid, UUID userUuid) {
        Address address = addressRepo.findByUuid(addressUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "uuid", addressUuid));

        if (!address.getUser().getUuid().equals(userUuid)) {
            throw new AccessDeniedException("You are not the owner of this address");
        }

        addressRepo.findByUserAndIsDefaultTrue(address.getUser())
                .ifPresent(existing -> existing.setDefault(false));

        address.setDefault(true);
        Address saved = addressRepo.save(address);
        return addressMapper.addressToDto(saved);
    }
}

