package com.eagledev.bookreaders.services.promocode;

import com.eagledev.bookreaders.dtos.commerce.PromocodeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PromocodeService {
    Page<PromocodeDto> getAll(Pageable pageable);
    PromocodeDto getByUuid(UUID uuid);
    PromocodeDto create(PromocodeDto request);
    PromocodeDto update(UUID uuid, PromocodeDto request);
    PromocodeDto setActive(UUID uuid, boolean active);
    void delete(UUID uuid);
    Page<PromocodeDto> searchByCode(String code , Pageable pageable);
}
