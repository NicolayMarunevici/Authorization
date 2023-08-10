package com.auth.moto.entity.dto;

public record LoginDto(
     String usernameOrEmail,
     String password
) {
}
