package com.eagledev.bookreaders.dtos.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token
) {
}
