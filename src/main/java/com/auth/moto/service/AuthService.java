package com.auth.moto.service;

import com.auth.moto.entity.dto.LoginDto;
import com.auth.moto.entity.dto.RegisterDto;

public interface AuthService {
  String login(LoginDto loginDto);
  String register(RegisterDto registerDto);
}
