package com.auth.moto.entity.dto;

import jakarta.validation.constraints.Size;

public record LoginDto(
    @Size(max = 10)
    String usernameOrEmail,
    String password
) {
}
