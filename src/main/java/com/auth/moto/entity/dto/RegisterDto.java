package com.auth.moto.entity.dto;


public record RegisterDto(
    String name,
    String username,
    String email,
    String password
) {
}
