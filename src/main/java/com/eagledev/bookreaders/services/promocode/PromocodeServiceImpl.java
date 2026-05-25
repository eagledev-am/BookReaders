package com.eagledev.bookreaders.services.promocode;

import com.eagledev.bookreaders.dtos.commerce.PromocodeDto;
import com.eagledev.bookreaders.entities.Promocode;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.PromocodeMapper;
import com.eagledev.bookreaders.repos.PromocodeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromocodeServiceImpl implements PromocodeService {

    private final PromocodeRepo promocodeRepo;
    private final PromocodeMapper promocodeMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PromocodeDto> getAll(Pageable pageable) {
        return promocodeRepo.findAll(pageable)
                .map(promocodeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PromocodeDto getByUuid(UUID uuid) {
        Promocode promocode = promocodeRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Promocode", "uuid", uuid));
        return promocodeMapper.toDto(promocode);
    }

    @Override
    @Transactional
    public PromocodeDto create(PromocodeDto request) {
        String normalizedCode = normalizeCode(request.getCode());

        if (promocodeRepo.existsByCodeIgnoreCase(normalizedCode)) {
            throw new IllegalArgumentException("Promocode with code '" + normalizedCode + "' already exists.");
        }

        Promocode promocode = Promocode.builder()
                .code(normalizedCode)
                .discountPercentage(request.getDiscountPercentage())
                .expirationDate(request.getExpirationDate())
                .isActive(request.isActive())
                .build();

        Promocode saved = promocodeRepo.save(promocode);
        return promocodeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public PromocodeDto update(UUID uuid, PromocodeDto request) {
        Promocode promocode = promocodeRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Promocode", "uuid", uuid));

        String normalizedCode = normalizeCode(request.getCode());
        boolean codeChanged = !promocode.getCode().equalsIgnoreCase(normalizedCode);

        if (codeChanged && promocodeRepo.existsByCodeIgnoreCase(normalizedCode)) {
            throw new IllegalArgumentException("Promocode with code '" + normalizedCode + "' already exists.");
        }

        promocode.setCode(normalizedCode);
        promocode.setDiscountPercentage(request.getDiscountPercentage());
        promocode.setExpirationDate(request.getExpirationDate());
        promocode.setActive(request.isActive());

        Promocode saved = promocodeRepo.save(promocode);
        return promocodeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public PromocodeDto setActive(UUID uuid, boolean active) {
        Promocode promocode = promocodeRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Promocode", "uuid", uuid));

        promocode.setActive(active);
        Promocode saved = promocodeRepo.save(promocode);
        return promocodeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        Promocode promocode = promocodeRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Promocode", "uuid", uuid));
        promocodeRepo.delete(promocode);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromocodeDto> searchByCode(String code , Pageable pageable) {
        String normalized = code == null ? "" : code.trim();
        if (normalized.isBlank()) {
            return getAll(pageable);
        }
        return promocodeRepo.findByCodeContainingIgnoreCase(normalized, pageable)
                .map(promocodeMapper::toDto);
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase();
    }
}
