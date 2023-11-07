package com.auth.moto.entity.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterDto(

    String name,
    @Size(max = 3)
    String username,

    String email,
    String password
) {
}
