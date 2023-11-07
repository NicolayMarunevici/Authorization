package com.auth.moto.service;

import com.auth.moto.entity.dto.LoginDto;
import com.auth.moto.entity.dto.RegisterDto;
import java.util.Map;

public interface AuthService {
  Map<String, String> login(LoginDto loginDto);

  String register(RegisterDto registerDto);

  Map<String, String> generateRefreshAndAccessTokenByExpireTime(String refreshToken, String accessToken);
}
